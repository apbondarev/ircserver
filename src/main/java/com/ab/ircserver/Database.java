package com.ab.ircserver;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

interface Database {
	
	User findOrCreateUser(String name, byte[] password);
	
	Room findOrCreateRoom(String roomName);
	
	void save(Room room);
	
}

class InMemoryDatabase implements Database {

	private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

	@Override
	public User findOrCreateUser(String name, byte[] password) {
		return users.computeIfAbsent(name, s -> new User(name, password));
	}
	
    @Override
    public Room findOrCreateRoom(String roomName) {
    	Objects.requireNonNull(roomName);
        if (roomName.length() == 0) {
            return Room.UNDEFINED;
        }
        return rooms.computeIfAbsent(roomName, Room::new).copy();
    }

    @Override
    public void save(Room room) {
        rooms.put(room.name(), room.copy());
    }

}
