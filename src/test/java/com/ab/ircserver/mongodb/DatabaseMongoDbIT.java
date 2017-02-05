package com.ab.ircserver.mongodb;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ab.ircserver.Message;
import com.ab.ircserver.Room;
import com.ab.ircserver.User;

import static org.junit.Assert.*;

import io.netty.channel.nio.NioEventLoopGroup;

public class DatabaseMongoDbIT {

    private DatabaseMongoDb db;

    private NioEventLoopGroup eventLoopGroup;

    @Before
    public void setUp() {
        eventLoopGroup = new NioEventLoopGroup(1);
        db = new DatabaseMongoDb(eventLoopGroup);
    }
    
    @After
    public void cleanUp() {
        eventLoopGroup.shutdownGracefully();
        if (db != null) {
            db.close();
        }
    }
    
    @Test
    public void findOrCreateUser() throws Exception {
        byte[] password = "password1".getBytes(StandardCharsets.UTF_8);
        CompletableFuture<User> future = (CompletableFuture<User>) db.findOrCreateUser("name1", password);
        User user = future.get();
        assertEquals("name1", user.name());
        assertTrue(user.isCorrectPassword(password));
    }
    
    @Test
    public void findOrCreateRoom() throws Exception {
        Room room = new Room("roomName1", Collections.emptyList());
        CompletableFuture<Void> futureSave = (CompletableFuture<Void>) db.save(room);
        futureSave.get();
        
        CompletableFuture<Room> futureFind = (CompletableFuture<Room>) db.findOrCreateRoom("roomName1");
        Room roomFound = futureFind.get();
        assertEquals("roomName1", roomFound.name());
        assertTrue(roomFound.lastMessages().isEmpty());
    }
    
    @Test
    public void save() throws Exception {
        Room room = new Room("roomName1", Arrays.asList(new Message("username1", "text1")));
        CompletableFuture<Void> futureSave = (CompletableFuture<Void>) db.save(room);
        futureSave.get();
        
        CompletableFuture<Room> futureFind = (CompletableFuture<Room>) db.findOrCreateRoom("roomName1");
        Room roomFound = futureFind.get();
        assertEquals("roomName1", roomFound.name());
        assertEquals(1, roomFound.lastMessages().size());
    }

}
