package com.ab.ircserver;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class Session {

    static final AttributeKey<Session> KEY_SESSION = AttributeKey.valueOf("session");
    static final AttributeKey<ChatState> KEY_STATE = AttributeKey.valueOf("state");

    private final Channel channel;

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
	
	public ChatState state() {
	    return channel.attr(KEY_STATE).get();
	}
	
	public Optional<String> username() {
	    Optional<User> user = state().user();
	    return user.map(User::name);
	}
	
	public void setState(ChatState state) {
		Attribute<ChatState> attrState = channel.attr(KEY_STATE);
		attrState.set(state);
	}
	
	private void sendNoFlush(Message msg) {
		channel.write(msg.username() + ": " + msg.text() + "\r\n");
	}
	
	public void send(Message msg) {
		sendNoFlush(msg);
		channel.flush();
	}
	
	public void send(Collection<Message> messages) {
		messages.forEach(this::sendNoFlush);
        channel.flush();
	}
	
	public void println(Stream<String> stream) {
		stream.forEach(u -> channel.write(u + "\r\n"));
		channel.flush();
	}

	public void println(String string) {
		channel.writeAndFlush(string + "\r\n");
	}

	void close(String message) {
		ChannelFuture future = channel.writeAndFlush(message);
		future.addListener(ChannelFutureListener.CLOSE);
	}

}
