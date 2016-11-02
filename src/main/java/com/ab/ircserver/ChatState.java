package com.ab.ircserver;

public interface ChatState {

	ChatState login(Session session, CommandLogin cmd);
	
	ChatState join(Session session, CommandJoin cmd);
	
	default ChatState leave(Session session) {
		session.leave();
		return StateDisconnected.INSTANCE;
	}

	ChatState printUsers(Session session);
	
	ChatState sendMessage(Session session, Message msg);
	
}
