package com.ab.ircserver;

class CommandUsers implements ChatCommand {

	@Override
	public ChatState exec(Session session, ChatState state) {
		return state.printUsers(session);
	}

}
