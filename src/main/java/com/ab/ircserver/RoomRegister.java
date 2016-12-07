package com.ab.ircserver;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface RoomRegister {

    Optional<Room> find(String roomName);

    Room findOrProduce(String roomName, Function<String, Room> producer);

    boolean remove(String roomName);

}

class RoomRegisterImpl implements RoomRegister {

    private final ConcurrentHashMap<String, Room> register = new ConcurrentHashMap<>();

    @Override
    public Optional<Room> find(String roomName) {
        return Optional.ofNullable(register.get(roomName));
    }
    
    @Override
    public Room findOrProduce(String roomName, Function<String, Room> producer) {
        return register.computeIfAbsent(roomName, producer);
    }
    
    @Override
    public boolean remove(String roomName) {
        return register.remove(roomName) != null;
    }
    
}