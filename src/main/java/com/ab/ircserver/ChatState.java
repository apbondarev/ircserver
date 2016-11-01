package com.ab.ircserver;

public interface ChatState {

	ChatState login(Session session, String name, byte[] password);
	
	ChatState join(Session session, String roomName);
	
	ChatState leave(Session session);
	
	ChatState printUsers(Session session);
	
	ChatState sendMessage(Session session, Message msg);
	
}
