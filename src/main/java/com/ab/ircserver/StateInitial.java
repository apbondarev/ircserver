package com.ab.ircserver;

public class StateInitial implements ChatState {
	
    public static final ChatState INSTANCE = new StateInitial();
    
    private UserRegister userRegister = null;
	
	private StateInitial() {
	    // Not used
	}

	@Override
	public ChatState login(Session session, String name, byte[] password) {
		try {
			User user = userRegister.login(name, password);
			session.auth(user);
			return StateLogedIn.INSTANCE;
		} catch (WrongPasswordException e) {
			session.println("Wrong password.");
			return this;
		}
	}

	@Override
	public ChatState join(Session session, String roomName) {
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
