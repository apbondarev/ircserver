package com.ab.ircserver;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.concurrent.EventExecutorGroup;

@RunWith(MockitoJUnitRunner.class)
public class DebouncerTest {
    
    private static final int DELAY = 200;
    Debouncer debouncer;
    EventExecutorGroup executor;
    
    @Mock
    Runnable task;
    
    @Before
    public void setUp() {
        executor = new DefaultEventLoopGroup(1);
        debouncer = new DebouncerImpl(DELAY, TimeUnit.MILLISECONDS, executor);
    }
    
    @After
    public void cleanUp() {
        executor.shutdownGracefully();
    }

    @Test
    public void testExec() throws Exception {
        long ts = System.currentTimeMillis();
        debouncer.exec(task);
        debouncer.exec(task);
        debouncer.exec(task);
        debouncer.exec(task);
        long te = System.currentTimeMillis();
        int elapsedTime = (int) (te-ts);
        
        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(DELAY));
        
        verify(task, times(elapsedTime / DELAY + 1)).run();
    }

}
