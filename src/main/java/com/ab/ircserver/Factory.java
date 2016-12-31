package com.ab.ircserver;

import java.util.concurrent.TimeUnit;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public interface Factory {

    Database database();
    
    Debouncer debouncer();
    
    EventLoopGroup eventLoopGroup();
    
    EventExecutorGroup executor();
    
    RoomRegister roomRegister();
    
    void shutdownGracefully();

}

class FactoryImpl implements Factory {

    private final Database db;
    
    private final EventLoopGroup eventExecutorGroup;
    
    private final EventExecutorGroup executor;

    private final RoomRegister roomReg;
    
    FactoryImpl(Database db, EventLoopGroup eventExecutorGroup) {
        super();
        this.db = db;
        this.roomReg = new RoomRegisterImpl();
        
        this.eventExecutorGroup = eventExecutorGroup;
        
        int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        this.executor = new DefaultEventExecutorGroup(threads);
    }

    @Override
    public Database database() {
        return db;
    }

    @Override
    public Debouncer debouncer() {
        return new DebouncerImpl(60, TimeUnit.SECONDS, executor());
    }

    @Override
    public EventLoopGroup eventLoopGroup() {
        return eventExecutorGroup;
    }

    @Override
    public EventExecutorGroup executor() {
        return executor;
    }

    @Override
    public RoomRegister roomRegister() {
        return roomReg;
    }

    @Override
    public void shutdownGracefully() {
        eventExecutorGroup.shutdownGracefully();
        executor.shutdownGracefully();
    }
    
}