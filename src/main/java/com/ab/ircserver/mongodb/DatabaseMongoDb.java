package com.ab.ircserver.mongodb;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.Binary;

import com.ab.ircserver.Database;
import com.ab.ircserver.Message;
import com.ab.ircserver.Room;
import com.ab.ircserver.User;
import com.mongodb.ServerAddress;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;

import io.netty.channel.EventLoopGroup;

public class DatabaseMongoDb implements Database {
    
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collectionUsers;
    private final MongoCollection<Document> collectionRooms;

    public DatabaseMongoDb(EventLoopGroup eventLoopGroup) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .clusterSettings(ClusterSettings.builder()
                        .hosts(asList(new ServerAddress("localhost")))
                        .description("Local Server")
                        .build())
                .streamFactoryFactory(NettyStreamFactoryFactory.builder()
                        .eventLoopGroup(eventLoopGroup)
                        .build())
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("ircserver");
        collectionUsers = database.getCollection("users");
        collectionRooms = database.getCollection("rooms");
    }

    @Override
    public CompletableFuture<User> findOrCreateUser(String name, byte[] password) {
        CompletableFuture<User> future = new CompletableFuture<>();
        collectionUsers.findOneAndUpdate(eq("name", name),
                combine(set("name", name),     
                        set("password", password)), 
                new FindOneAndUpdateOptions().upsert(true), 
                new SingleResultCallback<Document>() {
                    @Override
                    public void onResult(Document doc, Throwable e) {
                        if (e == null) {
                            User user;
                            if (doc == null) {
                                user = new User(name, password);
                            } else {
                                String nameNew = doc.get("name", String.class);
                                Binary passwordNew = doc.get("password", Binary.class);
                                user = new User(nameNew, passwordNew.getData());
                            }
                            future.complete(user);
                        } else {
                            future.completeExceptionally(e);
                        }
                    }
        });
        return future;
    }

    @Override
    public CompletableFuture<Room> findOrCreateRoom(String roomName) {
        CompletableFuture<Room> future = new CompletableFuture<>();
        collectionRooms.findOneAndUpdate(
                eq("name", roomName),
                combine(set("name", roomName),
                        set("messages", Collections.emptyList())), 
                new FindOneAndUpdateOptions().upsert(true), 
                new SingleResultCallback<Document>() {
                    @Override
                    public void onResult(Document doc, Throwable e) {
                        if (e == null) {
                            Room room;
                            if (doc == null) {
                                room = new Room(roomName, Collections.emptyList());
                            } else {
                                String nameNew = doc.get("name", String.class);
                                @SuppressWarnings("unchecked") List<Document> list = doc.get("messages", List.class);
                                List<Message> messages = list.stream()
                                    .map(d -> {
                                        String username = d.get("username", String.class);
                                        String text = d.get("text", String.class);
                                        return new Message(username, text);   
                                    })
                                    .collect(Collectors.toList());  
                                room = new Room(nameNew, messages);
                            }
                            future.complete(room);
                        } else {
                            future.completeExceptionally(e);
                        }
                    }
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> save(Room room) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        List<Document> messages = room.lastMessages().stream()
                .map(m -> new Document()
                        .append("username", m.username())
                        .append("text", m.text()))
                .collect(Collectors.toList());
                
        collectionRooms.updateOne(
                eq("name", room.name()), 
                combine(set("name", room.name()),
                        set("messages", messages)),
                new SingleResultCallback<UpdateResult>() {
                    @Override
                    public void onResult(UpdateResult result, Throwable e) {
                        if (e == null) {
                            future.complete(null);
                        } else {
                            future.completeExceptionally(e);
                        }
                    }
                });
        return future;
    }

    @Override
    public CompletableFuture<Void> close() {
        mongoClient.close();
        return CompletableFuture.completedFuture(null);
    }

}
