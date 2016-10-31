package com.ab.ircserver;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RoomRegister {

    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    
    public Room findOrCreate(String roomName) {
        if (roomName == null || roomName.length() == 0) {
            throw new IrcServerException("Incorrect channel: " + Objects.toString(roomName));
        }
        return rooms.computeIfAbsent(roomName, Room::new);
    }

}
