package com.ab.ircserver;

class CommandLogin implements ChatCommand {

	private final String userName;
	private final byte[] password;
	
	CommandLogin(String name, byte[] password) {
		this.userName = name;
		this.password = password;
	}
	
	String userName() {
		return userName;
	}
	
	byte[] password() {
		return password;
	}

	@Override
	public ChatState exec(Session session, ChatState state) {
		return state.login(session, this);
	}
	
}
