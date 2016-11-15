package com.ab.ircserver;

/**
 * Interface for chat commands.
 * @author albondarev
 */
@FunctionalInterface
public interface ChatCommand {
	void exec(Session session);
}

class CommandLogin implements ChatCommand {
	private final String userName;
	private final byte[] password;
	
	CommandLogin(String name, byte[] password) {
		this.userName = name;
		this.password = password;
	}
	
	String userName() {
		return userName;
	}
	
	byte[] password() {
		return password;
	}

	@Override
	public void exec(Session session) {
	    ChatState state = session.state();
	    state.login(session, user, this);
	}
}

class CommandJoin implements ChatCommand {
	private final String roomName;
	
	CommandJoin(String roomName) {
		this.roomName = roomName;
	}
	
	String roomName() {
		return roomName;
	}

	@Override
	public void exec(Session session) {
		ChatState state = session.state();
		state.join(session, newRoom);
	}
}

class CommandMessage implements ChatCommand {
	private final String text;
	
	CommandMessage(String text) {
		this.text = text;
	}
	
	String text() {
		return text;
	}

	@Override
	public void exec(Session session) {
		ChatState state = session.state();
		state.sendMessage(session, this);
	}
}

class CommandUsers implements ChatCommand {
	static final CommandUsers INSTANCE = new CommandUsers();
	
	private CommandUsers() {}
	
	@Override
	public void exec(Session session) {
		ChatState state = session.state();
		state.printUsers(session);
	}
}

class CommandWrong implements ChatCommand {
	static final CommandWrong LOGIN = new CommandWrong("Wrong command. Expected: /login name password");
	
	private final String message;
	
	private CommandWrong(String message) {
		super();
		this.message = message;
	}

	@Override
	public void exec(Session session) {
		session.println(message);
	}
}

class CommandLeave implements ChatCommand {
	static final CommandLeave INSTANCE = new CommandLeave();
	
	private CommandLeave() {}
	
	@Override
	public void exec(Session session) {
		ChatState state = session.state();
		state.leave(session);
	}
}
