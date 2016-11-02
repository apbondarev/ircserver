package com.ab.ircserver;

class CommandJoin implements ChatCommand {

	private final String roomName;
	
	CommandJoin(String roomName) {
		this.roomName = roomName;
	}
	
	String roomName() {
		return roomName;
	}

	@Override
	public ChatState exec(Session session, ChatState state) {
		return state.join(session, this);
	}
}
