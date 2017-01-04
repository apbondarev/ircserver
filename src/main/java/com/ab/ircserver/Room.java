package com.ab.ircserver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Room {

    public static final int CAPACITY = 10;

    public static final Room UNDEFINED = new Room("undefined");

    private final String name;
    private final Queue<Message> messages;
    private final Lock lock = new ReentrantLock();
    
    public Room(String name) {
        this(name, new ArrayDeque<>(CAPACITY));
    }
    
    public Room(String name, Collection<Message> messages) {
        this.name = name;
        this.messages = new ArrayDeque<>(messages);
    }

    public String name() {
        return name;
    }
    
    public void addMessage(Message msg) {
        lock.lock();
        try {
            while (messages.size() + 1 >= Room.CAPACITY) {
                messages.remove();
            }
            messages.add(msg);
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
    
    public Room copy() {
        lock.lock();
        try {
            return new Room(name, messages);
        } finally {
            lock.unlock();
        }
    }
}
