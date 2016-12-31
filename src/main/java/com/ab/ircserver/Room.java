package com.ab.ircserver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Room {

	public static final int CAPACITY = 10;

	public static final Room UNDEFINED = new Room("undefined");

	private final String name;
	private final Queue<Message> messages;
	
	private final ChannelGroup channels;
	private final Lock lock = new ReentrantLock();
	
	public Room(String name) {
		this(name, new ArrayDeque<>(CAPACITY));
	}
	
	public Room(String name, Collection<Message> messages) {
        this.name = name;
	    this.messages = new ArrayDeque<>(messages);
	    this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}
	
	public String name() {
		return name;
	}

	public boolean addSession(Session session) {
	    lock.lock();
	    try {
	        if (channels.size() >= CAPACITY) {
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
            return channels.remove(session.channel());
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

	public List<Message> lastMessages() {
		lock.lock();
		try {
		    List<Message> result = new ArrayList<>(messages.size());
			result.addAll(messages);
			return result;
		} finally {
			lock.unlock();
		}
	}

	public void send(Message msg) {
		lock.lock();
		try {
			while (messages.size() + 1 >= CAPACITY) {
				messages.remove();
			}
		    messages.add(msg);
		} finally {
			lock.unlock();
		}
		channels.writeAndFlush(msg.username() + ": " + msg.text() + "\r\n");
		save();
	}

	public void notifyMessage(String str) {
	    channels.writeAndFlush(str + "\r\n");
	}

    public Room copy() {
        lock.lock();
        try {
            return new Room(name, messages);
        } finally {
            lock.unlock();
        }
    }
    
    private void save() {
//        debouncer.exec(() -> {
//            Database db = factory.database();
//            db.save(this);
//        });
    }

}
