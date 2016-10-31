package com.ab.ircserver;

public interface ChatState {

	ChatState login(String name, byte[] password);
	
	ChatState join(String roomName);
	
	ChatState leave();
	
	ChatState printUsers();
	
	ChatState sendMessage(Message msg);
	
}
