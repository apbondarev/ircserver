package com.ab.ircserver;

/**
 * Interface for chat commands.
 * @author albondarev
 */
@FunctionalInterface
public interface ChatCommand {

	ChatState exec(Session session, ChatState state);

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
	public ChatState exec(Session session, ChatState state) {
		return state.login(session, this);
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
	public ChatState exec(Session session, ChatState state) {
		return state.join(session, this);
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
	public ChatState exec(Session session, ChatState state) {
		Message msg = new Message(session.user(), text);
		return state.sendMessage(session, msg);
	}
}

class CommandUsers implements ChatCommand {
	static final CommandUsers INSTANCE = new CommandUsers();
	
	private CommandUsers() {}
	
	@Override
	public ChatState exec(Session session, ChatState state) {
		return state.printUsers(session);
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
	public ChatState exec(Session session, ChatState state) {
		session.println(message);
		return state;
	}
}

class CommandLeave implements ChatCommand {
	static final CommandLeave INSTANCE = new CommandLeave();
	
	private CommandLeave() {}
	
	@Override
	public ChatState exec(Session session, ChatState state) {
		return state.leave(session);
	}
}
