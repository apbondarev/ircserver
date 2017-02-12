package com.ab.ircserver.cassandra;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ab.ircserver.Message;
import com.ab.ircserver.Room;
import com.ab.ircserver.User;

import static org.junit.Assert.*;

public class DatabaseCassandraIT {
    
    private static DatabaseCassandra db;

    @BeforeClass
    public static void connect() {
        db = new DatabaseCassandra("192.168.1.102");
        db.execute("delete from users where name='user1'");
        db.execute("delete from rooms where name='room1'");
    }
    
    @AfterClass
    public static void close() {
        if (db != null) db.close();
    }

    @Test
    public void testDatabaseCassandra() {
        db.testConnection();
    }
    
    @Test
    public void findOrCreateUser() throws Exception {
        // create non-existing user
        CompletableFuture<User> future = (CompletableFuture<User>) db.findOrCreateUser("name1", "password1".getBytes());
        User user = future.get();
        assertEquals("name1", user.name());
        
        // find existing user
        CompletableFuture<User> futureExists = (CompletableFuture<User>) db.findOrCreateUser("name1", "password1".getBytes());
        User userExists = futureExists.get();
        assertEquals("name1", userExists.name());
    }
    
    @Test
    public void findOrCreateRoom() throws Exception {
        CompletableFuture<Room> future = (CompletableFuture<Room>) db.findOrCreateRoom("room1");
        Room room = future.get();
        assertEquals("room1", room.name());
    }

    
    @Test
    public void save() throws Exception {
        List<Message> messages = Arrays.asList(
                new Message("username1", "text1"),
                new Message("username2", "text2"),
                new Message("username3", "text3"));
        Room room = new Room("room1", messages);
        CompletableFuture<Void> future = (CompletableFuture<Void>) db.save(room);
        future.get();
        assertTrue(future.isDone());
    }
}
