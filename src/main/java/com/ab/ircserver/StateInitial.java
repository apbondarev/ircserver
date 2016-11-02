package com.ab.ircserver;

public class StateInitial implements ChatState {
	
    public static final ChatState INSTANCE = new StateInitial();
    
	private StateInitial() {
	    // Not used
	}

	@Override
	public ChatState login(Session session, CommandLogin cmd) {
		session.login(cmd.userName(), cmd.password());
		return StateLogedIn.INSTANCE;
	}

	@Override
	public ChatState join(Session session, CommandJoin cmd) {
		session.println("Start with: /login name password");
		return this;
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
