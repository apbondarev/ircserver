package com.ab.ircserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Room {

	public static final int CAPACITY = 10;

	public static final Room UNDEFINED = new Room("undefined");

	private static final int LAST_MESSAGES_COUNT = 10;

	private final String name;
	private final BlockingQueue<Session> sessions = new ArrayBlockingQueue<>(CAPACITY);
	private final List<Message> messages = new ArrayList<>();
	private final Lock lock = new ReentrantLock();

	public Room(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean addSession(Session session) {
		return sessions.offer(session);
	}

	public boolean removeSession(Session session) {
		return sessions.remove(session);
	}

	public List<String> users() {
		return sessions.stream().map(s -> s.user().name()).collect(Collectors.toList());
	}

	public void add(Message msg) {
		lock.lock();
		try {
			if (messages.size() > LAST_MESSAGES_COUNT) {
				messages.remove(0);
			}
			messages.add(msg);
		} finally {
			lock.unlock();
		}
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
		sessions.stream()
			.filter(s -> !s.user().name().equals(msg.from().name()))
			.forEach(s -> {
				s.send(msg);
				s.flush();
			});
	}
}
