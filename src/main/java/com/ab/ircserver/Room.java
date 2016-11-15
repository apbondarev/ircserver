package com.ab.ircserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Room {

	public static final int CAPACITY = 10;

	public static final Room UNDEFINED = new Room("undefined");

	private final String name;
	private final BlockingQueue<Session> sessions = new ArrayBlockingQueue<>(CAPACITY);
	private final List<Message> messages = new ArrayList<>();
	private final Lock lock = new ReentrantLock();

	public Room(String name) {
		this.name = name;
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
		List<Message> result = new ArrayList<>();
		lock.lock();
		try {
			result.addAll(messages);
		} finally {
			lock.unlock();
		}
		return result;
	}

	public void send(Message msg) {
		lock.lock();
		try {
			while (messages.size() >= CAPACITY) {
				messages.remove(0);
			}
		    messages.add(msg);
		} finally {
			lock.unlock();
		}
		sessions.stream()
			.forEach( s -> s.send(msg) );
	}

	public void notifyMessage(String str) {
		sessions.stream()
			.forEach(s -> {
				s.println(str);
			});		
	}
}
