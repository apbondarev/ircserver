package com.ab.ircserver;

import java.util.List;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class StateJoined implements ChatState {
	
	private final Session session;
	private RoomRegister roomRegister = null;

	public StateJoined(Session session) {
		this.session = session;
	}

	@Override
	public ChatState login(String name, byte[] password) {
		session.println("You've logged in already as user '" + session.user().name() + "'");
		return this;
	}

	@Override
	public ChatState join(String roomName) {
		Room room = roomRegister.findOrCreate(roomName);
		session.join(room);
		List<Message> lastMessages = room.lastMessages();
		lastMessages.forEach(session::send);
		session.flush();
		return this;
	}

	@Override
	public ChatState leave() {
		ChannelFuture future = session.leave();
		future.addListener(ChannelFutureListener.CLOSE);
		return new StateDisconnected();
	}

	@Override
	public ChatState printUsers() {
		session.printUsers();
		return this;
	}

	@Override
	public ChatState sendMessage(Message msg) {
		session.sendInRoom(msg);
		return this;
	}

}
