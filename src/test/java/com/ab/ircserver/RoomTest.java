package com.ab.ircserver;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.netty.channel.Channel;

@RunWith(MockitoJUnitRunner.class)
public class RoomTest {
    
    @Mock
    Channel channel1;
    
    @Mock
    Channel channel2;

    @Test
    public void testUsers() {
        Session s1 = Session.anonimous(channel1);
        s1.login("user1", "password1".getBytes());
        
        Session s2 = Session.anonimous(channel2);
        s2.login("user2", "password2".getBytes());
        
        Room room = new Room("room1");
        assertEquals(0, room.users().size());
        
        room.addSession(s1);
        room.addSession(s2);
        
        assertEquals(2, room.users().size());
        assertEquals("user1", room.users().get(0));
        assertEquals("user2", room.users().get(1));
        
        room.removeSession(s1);
        assertEquals(1, room.users().size());
        assertEquals("user2", room.users().get(0));
    }

}
