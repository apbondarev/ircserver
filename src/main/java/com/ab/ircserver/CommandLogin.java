package com.ab.ircserver;

class CommandLogin implements ChatCommand {

	private final String userName;
	
	CommandLogin(String name) {
		this.userName = name;
	}
	
	String userName() {
		return userName;
	}
}
