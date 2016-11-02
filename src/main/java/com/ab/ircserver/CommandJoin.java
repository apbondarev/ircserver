package com.ab.ircserver;

class CommandJoin implements ChatCommand {

	private final String roomName;
	
	CommandJoin(String name) {
		this.roomName = name;
	}
	
	String roomName() {
		return roomName;
	}
}
