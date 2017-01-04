package com.ab.ircserver;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface RoomRegister {

    Optional<RoomContext> find(String roomName);

    RoomContext findOrProduce(String roomName, Function<String, RoomContext> producer);

    void remove(String name);

}

class RoomRegisterImpl implements RoomRegister {

    private final ConcurrentHashMap<String, RoomContext> register = new ConcurrentHashMap<>();
    
    @Override
    public Optional<RoomContext> find(String roomName) {
        return Optional.ofNullable(register.get(roomName));
    }
    
    @Override
    public RoomContext findOrProduce(String roomName, Function<String, RoomContext> producer) {
        return register.computeIfAbsent(roomName, producer::apply);
    }

    @Override
    public void remove(String name) {
        register.remove(name);
    }
        
}