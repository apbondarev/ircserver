package com.ab.ircserver;

public class StateLogedIn implements ChatState {
	
    public static final ChatState INSTANCE = new StateLogedIn();
            
	private StateLogedIn() {
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
        return StateJoined.INSTANCE;
	}

	@Override
	public ChatState printUsers(Session session) {
		session.println("Start with: /login name password");
		return this;
	}

	@Override
	public ChatState sendMessage(Session session, Message msg) {
		session.println("Start with: /login name password");
		return this;
	}

}
