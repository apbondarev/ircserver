package com.ab.ircserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public interface Database {
	
    CompletionStage<User> findOrCreateUser(String name, byte[] password);
	
    CompletionStage<Room> findOrCreateRoom(String roomName);
	
    CompletionStage<Void> save(Room room);
	
    CompletionStage<Void> close();
	
}

class InMemoryDatabase implements Database {

	private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	
	private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

	@Override
	public CompletionStage<User> findOrCreateUser(String name, byte[] password) {
		User user = users.computeIfAbsent(name, s -> new User(name, password));
		return CompletableFuture.completedFuture(user);
	}
	
    @Override
    public CompletionStage<Room> findOrCreateRoom(String roomName) {
        Room room = rooms.computeIfAbsent(roomName, Room::new).copy();
        return CompletableFuture.completedFuture(room);
    }

    @Override
    public CompletionStage<Void> save(Room room) {
        rooms.put(room.name(), room.copy());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> close() {
        users.clear();
        rooms.clear();
        return CompletableFuture.completedFuture(null);
    }

}
