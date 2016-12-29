package com.ab.ircserver;

import java.util.Collection;
import java.util.Optional;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class Session {

    static final AttributeKey<Session> KEY_SESSION = AttributeKey.valueOf("session");

    private final Channel channel;
    private ChatState state;

	private Session(Channel channel) {
		this.channel = channel;
	}
	
	public static void newSession(Channel channel) {
	    Session session = new Session(channel);
	    session.setState(new Initial());
	    Attribute<Session> attrSession = channel.attr(KEY_SESSION);
	    attrSession.set(session);
	    session.println("You are connected to IRC server.");
	}
	
	public static Session current(Channel channel) {
	    return channel.attr(KEY_SESSION).get();
	}
	
	public Channel channel() {
	    return channel;
	}
	
	public Optional<String> username() {
	    Optional<User> user = state.user();
	    return user.map(User::name);
	}
	
	public void setState(ChatState state) {
        this.state = state;
	}
	
	private void sendNoFlush(Message msg) {
		channel.write(msg.username() + ": " + msg.text() + "\r\n");
	}
	
	public void send(Collection<Message> messages) {
		messages.forEach(this::sendNoFlush);
        channel.flush();
	}
	
	public void println(String string) {
		channel.writeAndFlush(string + "\r\n");
	}

	void close(String message) {
		ChannelFuture future = channel.writeAndFlush(message);
		future.addListener(ChannelFutureListener.CLOSE);
	}

    public void login(User user, byte[] password) {
        state.login(this, user, password);
    }

    public void join(Room room) {
        state.join(this, room);
    }

    public void sendMessage(String text) {
        state.sendMessage(this, text);
    }

    public void printUsers() {
        state.printUsers(this);
    }

    public void leave() {
        state.leave(this);
    }

}
