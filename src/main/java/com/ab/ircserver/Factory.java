package com.ab.ircserver;

import java.util.concurrent.TimeUnit;

import io.netty.util.concurrent.EventExecutorGroup;

public interface Factory {

    Database database();
    
    Debouncer debouncer();
    
    EventExecutorGroup executor();
    
    RoomRegister roomRegister();
    
}

class FactoryImpl implements Factory {

    private final Database db;
    
    private final EventExecutorGroup executor;

    private final RoomRegister roomReg;
    
    FactoryImpl(Database db, EventExecutorGroup executor, RoomRegister roomReg) {
        super();
        this.db = db;
        this.executor = executor;
        this.roomReg = roomReg;
    }

    @Override
    public Database database() {
        return db;
    }

    @Override
    public Debouncer debouncer() {
        return new Debouncer(60, TimeUnit.SECONDS, executor());
    }

    @Override
    public EventExecutorGroup executor() {
        return executor;
    }

    @Override
    public RoomRegister roomRegister() {
        return roomReg;
    }
    
}