package com.ab.ircserver;

class CommandLeave implements ChatCommand {

	private final String roomName;
	
	CommandLeave(String name) {
		this.roomName = name;
	}
	
	String roomName() {
		return roomName;
	}
}
