package com.ab.ircserver;

public class StateJoined implements ChatState {
    
    public static final ChatState INSTANCE = new StateJoined();
	
	private StateJoined() {
	    // Not used
	}
	
	@Override
	public ChatState login(Session session, CommandLogin cmd) {
		session.println("You've logged in already as user '" + session.user().name() + "'");
		return this;
	}

	@Override
	public ChatState join(Session session, CommandJoin cmd) {
		session.join(cmd.roomName());
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
