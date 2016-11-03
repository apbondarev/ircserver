package com.ab.ircserver;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegister {

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    
    public UserRegister() {
        // 
    }
    
    public User login(String name, byte[] password) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(password);
        
        User user = users.computeIfAbsent(name, s -> new User(name, password));
        if (!user.isCorrectPassword(name, password)) { 
            return User.ANONIMOUS;
        }
        return user;
    }

}
