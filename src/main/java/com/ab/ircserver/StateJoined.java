package com.ab.ircserver;

import java.util.List;

public class StateJoined implements ChatState {
    
    public static final ChatState INSTANCE = new StateJoined();
	
	private RoomRegister roomRegister = null;

	private StateJoined() {
	    // Not used
	}
	
	@Override
	public ChatState login(Session session, String name, byte[] password) {
		session.println("You've logged in already as user '" + session.user().name() + "'");
		return this;
	}

	@Override
	public ChatState join(Session session, String roomName) {
		Room room = roomRegister.findOrCreate(roomName);
		boolean ok = session.join(room);
		if (ok) {
    		List<Message> lastMessages = room.lastMessages();
    		lastMessages.forEach(session::send);
    		session.flush();
		} else {
		    session.println("Max 10 active clients per channel is allowed.");
		}
		return this;
	}

	@Override
	public ChatState printUsers(Session session) {
		session.printUsers();
		return this;
	}

	@Override
	public ChatState sendMessage(Session session, Message msg) {
		session.sendInRoom(msg);
		return this;
	}

}
