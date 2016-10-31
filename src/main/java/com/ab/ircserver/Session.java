package com.ab.ircserver;

import java.util.Objects;

import io.netty.channel.Channel;

public class Session {

    private final User user;
    private final Channel channel;
    private Room room = Room.UNDEFINED;
    
    public Session(User user, Channel channel) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(channel);
        this.user = user;
        this.channel = channel;
    }
    
    public User user() {
        return user;
    }

    public Channel channel() {
        return channel;
    }
    
    public Room room() {
        return room;
    }
    
    public boolean join(Room newRoom) {
        if (newRoom.addSession(this)) {
            room.removeSession(this);
            room = newRoom;
            return true;
        } else {
            return false;
        }
    }
    
    public void leave() {
        room.removeSession(this);
        room = Room.UNDEFINED;
    }
    
}
