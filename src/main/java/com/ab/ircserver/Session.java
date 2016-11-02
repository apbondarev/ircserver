package com.ab.ircserver;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class Session {

	private static UserRegister userRegister = new UserRegister();
	private static RoomRegister roomRegister = new RoomRegister();

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
	
	public void login(String userName, byte[] password) {
		User userNew = userRegister.login(userName, password);
		this.user = userNew;
	    Arrays.fill(password, (byte) 0);
	    channel.writeAndFlush("Welcome " + user.name() + "!\r\n");
	}
	
	public void printUsers() {
		room.users().forEach(u -> channel.write(u + "\r\n"));
		channel.flush();
	}

	public void join(String roomName) {
		Room newRoom = roomRegister.findOrCreate(roomName);
		if (newRoom.addSession(this)) {
			room.removeSession(this);
			room.notifyMessage("User '" + user.name() + "' has left the channel '" + room.name() + "'");
			room = newRoom;
			room.notifyMessage("User '" + user.name() + "' has joined the channel '" + newRoom.name() + "'");
			List<Message> lastMessages = room.lastMessages();
            lastMessages.forEach(this::send);
            flush();
		} else {
			throw new MaxActiveClientsException("Max 10 active clients per channel is allowed.");
		}
	}

	public void leave() {
		room.removeSession(this);
		room.notifyMessage("User '" + user.name() + "' has left the channel '" + room.name() + "'");
		room = Room.UNDEFINED;
		ChannelFuture future = channel.writeAndFlush("Have a good day!\r\n");
		future.addListener(ChannelFutureListener.CLOSE);
	}

	public void send(Message msg) {
		channel.write(msg.from().name() + ": " + msg.text() + "\r\n");
	}
	
	public void flush() {
		channel.flush();
	}

	public void sendInRoom(Message msg) {
		room.send(msg);
	}

	public void println(String string) {
		channel.writeAndFlush(string + "\r\n");
	}

}
