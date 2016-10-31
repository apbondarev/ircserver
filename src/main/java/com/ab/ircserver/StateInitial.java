package com.ab.ircserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class StateInitial implements ChatState {
	
	private final Session session;
	private UserRegister userRegister = null;

	public StateInitial(Session session) {
		this.session = session;
	}

	@Override
	public ChatState login(String name, byte[] password) {
		try {
			User user = userRegister.login(name, password);
			Session newSession = new Session(user, session.channel());
			newSession.printWelcome();
			return new StateLogedIn(newSession);
		} catch (WrongPasswordException e) {
			session.println("Wrong password.");
			return this;
		}
	}

	@Override
	public ChatState join(String roomName) {
		session.println("Start with: /login name password");
		return this;
	}

	@Override
	public ChatState leave() {
		ChannelFuture future = session.leave();
		future.addListener(ChannelFutureListener.CLOSE);
		return new StateDisconnected();
	}

	@Override
	public ChatState printUsers() {
		session.println("Start with: /login name password");
		return this;
	}

	@Override
	public ChatState sendMessage(Message msg) {
		session.println("Start with: /login name password");
		return this;
	}

}
