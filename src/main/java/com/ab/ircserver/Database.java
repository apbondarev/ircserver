package com.ab.ircserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public interface Database {
	
    CompletableFuture<User> findOrCreateUser(String name, byte[] password);
	
    CompletableFuture<Room> findOrCreateRoom(String roomName);
	
    CompletableFuture<Void> save(Room room);
	
    CompletableFuture<Void> close();
	
}

class InMemoryDatabase implements Database {

	private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	
	private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

	@Override
	public CompletableFuture<User> findOrCreateUser(String name, byte[] password) {
		User user = users.computeIfAbsent(name, s -> new User(name, password));
		return CompletableFuture.completedFuture(user);
	}
	
    @Override
    public CompletableFuture<Room> findOrCreateRoom(String roomName) {
        Room room = rooms.computeIfAbsent(roomName, Room::new).copy();
        return CompletableFuture.completedFuture(room);
    }

    @Override
    public CompletableFuture<Void> save(Room room) {
        rooms.put(room.name(), room.copy());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> close() {
        users.clear();
        rooms.clear();
        return CompletableFuture.completedFuture(null);
    }

}
