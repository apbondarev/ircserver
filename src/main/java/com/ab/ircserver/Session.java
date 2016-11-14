package com.ab.ircserver;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class Session {

    static final AttributeKey<Session> KEY_SESSION = AttributeKey.valueOf("session");

	private User user = User.ANONIMOUS;
	private Room room = Room.UNDEFINED;
	private final Channel channel;
	private ChatState state = ChatState.INITIAL;

	private Session(Channel channel) {
		Objects.requireNonNull(channel);
		this.channel = channel;
	}
	
	public static void newSession(Channel channel) {
	    Session session = new Session(channel);
	    Attribute<Session> attrSession = channel.attr(KEY_SESSION);
	    attrSession.set(session);
	    session.println("You are connected to IRC server.");
	}
	
	public static Session current(Channel channel) {
	    return channel.attr(KEY_SESSION).get();
	}
	
	public void exec(ChatCommand cmd) {
	    cmd.exec(this);
	}
	
	public String username() {
	    return user.name();
	}
	
	public boolean login(User user, byte[] password) {
		if (!user.isCorrectPassword(password)) {
			println("Wrong password");
			return false;
		}
		
		this.user = user;
	    this.state = ChatState.LOGGED_IN;
	    println("Welcome " + user.name() + "!");
	    return true;
	}
	
	public void printUsers() {
		room.users().forEach(u -> channel.write(u + "\r\n"));
		channel.flush();
	}

	public boolean join(Room newRoom) {
		if (newRoom == Room.UNDEFINED) {
			println("Wrong channel '" + newRoom.name() + "'.");
			return false;
		} else if (newRoom.addSession(this)) {
			room.removeSession(this);
			room.notifyMessage("User '" + user.name() + "' has left the channel '" + room.name() + "'");
			room = newRoom;
			room.notifyMessage("User '" + user.name() + "' has joined the channel '" + newRoom.name() + "'");
			List<Message> lastMessages = room.lastMessages();
			send(lastMessages);
			state = ChatState.JOINED;
			return true;
		} else {
			println("Max " + Room.CAPACITY + " active clients per channel is allowed.");
			return false;
		}
	}

	public void leave() {
		room.removeSession(this);
		room.notifyMessage("User '" + user.name() + "' has left the channel '" + room.name() + "'");
		room = Room.UNDEFINED;
		state = ChatState.DISCONNECTED;
		ChannelFuture future = channel.writeAndFlush("Have a good day!\r\n");
		future.addListener(ChannelFutureListener.CLOSE);
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

	public void sendInRoom(Message msg) {
		room.send(msg);
	}

	public void println(String string) {
		channel.writeAndFlush(string + "\r\n");
	}

}
