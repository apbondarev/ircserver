package com.ab.ircserver;

import java.util.Objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Session {

	private User user = User.ANONIMOUS;
	private final Channel channel;
	private Room room = Room.UNDEFINED;

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
	
	public void auth(User user) {
	    this.user = user;
	    channel.writeAndFlush("Welcome " + user.name() + "!\r\n");
	}
	
	public Channel channel() {
		return channel;
	}
	
	public void printUsers() {
		room.users().forEach(u -> channel.write(u + "\r\n"));
		channel.flush();
	}

	public boolean join(Room newRoom) {
		if (newRoom.addSession(this)) {
			room.removeSession(this);
			room = newRoom;
			channel.write("You are in room " + newRoom.getName() + "\r\n");
			return true;
		} else {
			return false;
		}
	}

	public ChannelFuture leave() {
		room.removeSession(this);
		room = Room.UNDEFINED;
		return channel.writeAndFlush("Have a good day!\r\n");
	}

	public void send(Message msg) {
		room.add(msg);
		channel.write(msg.from().name() + ": " + msg.text() + "\r\n");
	}
	
	public void flush() {
		channel.flush();
	}

	public void sendInRoom(Message msg) {
		room.send(msg);
	}

	public boolean isAnonimous() {
		return user == User.ANONIMOUS;
	}

	public boolean inRoom() {
		return room != Room.UNDEFINED;
	}

	public void println(String string) {
		channel.writeAndFlush(string + "\r\n");
	}

}
