package com.ab.ircserver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Room {

	public static final int CAPACITY = 10;

	public static final Room UNDEFINED = new Room("undefined");

	private final String name;
	private final Queue<Message> messages;
	
	private final BlockingQueue<Session> sessions = new ArrayBlockingQueue<>(CAPACITY);
	private final Lock lockMessages = new ReentrantLock();

	public Room(String name) {
		this.name = name;
		messages = new ArrayDeque<>(CAPACITY);
	}
	
	private Room(String name, Collection<Message> messages) {
        this.name = name;
	    this.messages = new ArrayDeque<>(messages);
	}
	
	public String name() {
		return name;
	}

	public boolean addSession(Session session) {
		return sessions.offer(session);
	}

	public boolean removeSession(Session session) {
		return sessions.remove(session);
	}

	public List<String> users() {
		return sessions.stream()
		        .map(Session::username)
		        .filter(Optional::isPresent)
		        .map(Optional::get)
		        .collect(Collectors.toList());
	}

	public List<Message> lastMessages() {
		lockMessages.lock();
		try {
		    List<Message> result = new ArrayList<>(messages.size());
			result.addAll(messages);
			return result;
		} finally {
			lockMessages.unlock();
		}
	}

	public void send(Message msg) {
		lockMessages.lock();
		try {
			while (messages.size() + 1 >= CAPACITY) {
				messages.remove();
			}
		    messages.add(msg);
		} finally {
			lockMessages.unlock();
		}
		sessions.stream()
			.forEach( s -> s.send(msg) );
	}

	public void notifyMessage(String str) {
		sessions.stream()
			.forEach( s -> s.println(str) );		
	}

    public Room copy() {
        lockMessages.lock();
        try {
            return new Room(name, messages);
        } finally {
            lockMessages.unlock();
        }
    }

}
