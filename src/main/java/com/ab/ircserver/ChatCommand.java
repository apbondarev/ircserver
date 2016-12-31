package com.ab.ircserver;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for chat commands.
 * @author albondarev
 */
public interface ChatCommand {

    void exec(Session session);
    
}

class CommandLogin implements ChatCommand {
	private final String userName;
	private final byte[] password;
	private final Database db;
	
	CommandLogin(String name, byte[] password, Factory factory) {
		this.userName = name;
		this.password = password;
		this.db = factory.database();
	}
	
	@Override
	public void exec(Session session) {
	    CompletableFuture<User> future = db.findOrCreateUser(userName, password);
        future.whenComplete((user, e) -> {
            if (e == null) {
                session.login(user, password);
            } else {
                session.println(e.getMessage());
            }
        });
	}
}

class CommandJoin implements ChatCommand {
	private final String roomName;
	private final RoomRegister roomReg;
    private final Database db;
	
	CommandJoin(String roomName, Factory factory) {
        this.roomName = roomName;
        this.roomReg = factory.roomRegister();
        this.db = factory.database();
	}
	
	@Override
	public void exec(Session session) {
	    Optional<Room> room = roomReg.find(roomName);
	    if (room.isPresent()) {
	        session.join(room.get());
	    } else {
	        CompletableFuture<Room> future = db.findOrCreateRoom(roomName);
	        future.whenComplete((oldOrNewRoom, e) -> {
	            if (e == null) {
	                Room roomNew = roomReg.findOrProduce(roomName, r -> oldOrNewRoom);
	                session.join(roomNew);
	            } else {
	                session.println(e.getMessage());
	            }
	        });
	    }
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
		session.sendMessage(text);
	}
}

class CommandUsers implements ChatCommand {
	static final CommandUsers INSTANCE = new CommandUsers();
	
	private CommandUsers() {}
	
	@Override
	public void exec(Session session) {
		session.printUsers();
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
		session.leave();
	}
}
