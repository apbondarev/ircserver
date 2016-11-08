package com.ab.ircserver;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class Session {

	private User user = User.ANONIMOUS;
	private Room room = Room.UNDEFINED;
	private final Channel channel;

	private Session(Channel channel) {
		Objects.requireNonNull(channel);
		this.channel = channel;
	}
	
	public static Session anonimous(Channel channel) {
		return new Session(channel);
	}

	public User user() {
		return user;
	}
	
	public boolean login(User user, byte[] password) {
		if (!user.isCorrectPassword(password)) {
			println("Wrong password");
			return false;
		}
		
		this.user = user;
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
		ChannelFuture future = channel.writeAndFlush("Have a good day!\r\n");
		future.addListener(ChannelFutureListener.CLOSE);
	}

	private void sendNoFlush(Message msg) {
		channel.write(msg.from().name() + ": " + msg.text() + "\r\n");
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
