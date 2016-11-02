package com.ab.ircserver;

class CommandMessage implements ChatCommand {

	private final String text;
	
	CommandMessage(String text) {
		this.text = text;
	}
	
	String text() {
		return text;
	}

	@Override
	public ChatState exec(Session session, ChatState state) {
		Message msg = new Message(session.user(), text);
		return state.sendMessage(session, msg);
	}
}
