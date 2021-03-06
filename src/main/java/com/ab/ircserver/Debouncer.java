package com.ab.ircserver;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;

public interface Debouncer {
    
    void exec(Runnable task);
    
}

class DebouncerImpl implements Debouncer {

    private final long delay;
    
    private final TimeUnit timeUnit;
    
    private final EventExecutorGroup executor;

    private long execTime; 
    
    private Lock lock = new ReentrantLock();
    
    public DebouncerImpl(long delay, TimeUnit timeUnit, EventExecutorGroup executor) {
        super();
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.executor = executor;
    }

    public void exec(Runnable task) {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= execTime) {
                execTime = currentTime + timeUnit.toMillis(delay);
                ScheduledFuture<?> future = executor.schedule(task, delay, timeUnit);
                future.addListener(f -> {
                    if (f.isDone() && !f.isSuccess()) {
                        System.out.println(f.cause().getMessage());
                    }
                });
            }
        } finally {
            lock.unlock();
        }
    }

}
