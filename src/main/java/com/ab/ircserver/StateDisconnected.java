package com.ab.ircserver;

public class StateDisconnected implements ChatState {
	
	public StateDisconnected() {
		//
	}

	@Override
	public ChatState login(String name, byte[] password) {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState join(String roomName) {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState leave() {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState printUsers() {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState sendMessage(Message msg) {
		throw new IrcServerException("Client disconnected");
	}

}
