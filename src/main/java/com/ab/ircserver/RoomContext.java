package com.ab.ircserver;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RoomContext {

    private final Room room;
	private final ChannelGroup channels;
	private final Lock lock = new ReentrantLock();
	
	private final Debouncer debouncer;
    private final Database db;
    private final RoomRegister roomRegister;
	
	public RoomContext(Room room, Factory factory) {
        this.room = room;
	    this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	    this.debouncer = factory.debouncer();
	    this.db = factory.database();
	    this.roomRegister = factory.roomRegister();
	}
	
	public String name() {
	    return room.name();
	}
	
	public Room room() {
	    return room;
	}
	
	public boolean addSession(Session session) {
	    lock.lock();
	    try {
	        if (channels.size() >= Room.CAPACITY) {
	            return false;
	        }
	        return channels.add(session.channel());
	    } finally {
	        lock.unlock();
	    }
	}

	public boolean removeSession(Session session) {
	    lock.lock();
        try {
            boolean removed = channels.remove(session.channel());
            onRemoveUser();
            return removed;
        } finally {
            lock.unlock();
        }
	}

	public List<String> users() {
	    lock.lock();
        try {
            return channels.stream()
                .map(c -> Session.current(c).username())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
	}

	public void send(Message msg) {
		room.addMessage(msg);
		channels.writeAndFlush(msg.username() + ": " + msg.text() + "\r\n");
		onSendMessage();
	}

	public void notifyMessage(String str) {
	    channels.writeAndFlush(str + "\r\n");
	}

    private void onSendMessage() {
        debouncer.exec( () -> db.save(room) );
    }

    private void onRemoveUser() {
        debouncer.exec(() -> {
            CompletableFuture<Void> future = db.save(room);
            future.whenComplete((r, e) -> {
                if (e == null) {
                    removeIfEmpty();
                } else {
                    e.printStackTrace();
                }
            });
        });
    }

    private void removeIfEmpty() {
        lock.lock();
        try {
            if (channels.isEmpty()) {
                roomRegister.remove(room.name());
            }
        } finally {
            lock.unlock();
        }
    }

}
