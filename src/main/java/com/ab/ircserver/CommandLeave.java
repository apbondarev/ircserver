package com.ab.ircserver;

class CommandLeave implements ChatCommand {

	@Override
	public ChatState exec(Session session, ChatState state) {
		return state.leave(session);
	}
	
}
