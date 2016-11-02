package com.ab.ircserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

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
	public ChatState leave(Session session) {
		ChannelFuture future = session.leave();
		future.addListener(ChannelFutureListener.CLOSE);
		return StateDisconnected.INSTANCE;
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
