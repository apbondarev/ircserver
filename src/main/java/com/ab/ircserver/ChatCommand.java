package com.ab.ircserver;

/**
 * Interface for chat commands.
 * @author albondarev
 */
public interface ChatCommand {

    default boolean isLongRunning() {
        return false;
    }
    
    void exec(Session session);
}

class CommandLogin implements ChatCommand {
	private final String userName;
	private final byte[] password;
	private final Database db;
	
	CommandLogin(Database db, String name, byte[] password) {
	    this.db = db;
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
	public boolean isLongRunning() {
        return true;
    }

	@Override
	public void exec(Session session) {
        User user = db.findOrCreateUser(userName, password);

        ChatState state = session.state();
        state.login(session, user, this);
	}
}

class CommandJoin implements ChatCommand {
    private final Database db;
	private final String roomName;
	
	CommandJoin(Database db, String roomName) {
		this.db = db;
        this.roomName = roomName;
	}
	
	String roomName() {
		return roomName;
	}

	@Override
    public boolean isLongRunning() {
        return true;
    }
	
	@Override
	public void exec(Session session) {
	    Room room = db.findOrCreateRoom(roomName);
	    
	    ChatState state = session.state();
        state.join(session, room);
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
