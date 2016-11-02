package com.ab.ircserver;

class CommandWrong implements ChatCommand {
	
	private String message;
	
	public CommandWrong(String message) {
		super();
		this.message = message;
	}

	@Override
	public ChatState exec(Session session, ChatState state) {
		throw new WrongCommandException(message);
	}

}
