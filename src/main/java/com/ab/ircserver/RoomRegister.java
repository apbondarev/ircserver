package com.ab.ircserver;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RoomRegister {

    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    
    public Room findOrCreate(String roomName) {
    	Objects.requireNonNull(roomName);
        if (roomName.length() == 0) {
            return Room.UNDEFINED;
        }
        return rooms.computeIfAbsent(roomName, Room::new);
    }

}
