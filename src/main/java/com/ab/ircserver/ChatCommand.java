package com.ab.ircserver;

import java.util.Optional;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;

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
	private final EventExecutorGroup executor;
	
	CommandLogin(String name, byte[] password, Factory factory) {
		this.userName = name;
		this.password = password;
		this.db = factory.database();
		this.executor = factory.executor();
	}
	
	@Override
	public void exec(Session session) {
	    Future<?> future = executor.submit(() -> {
	        User user = db.findOrCreateUser(userName, password);
	        session.login(user, password);
	    });
	    future.addListener(f -> {
	        if (f.isDone() && !f.isSuccess()) {
	            if (f.isCancelled()) {
	                session.println("Operation has been cancelled");
	            } else {
	                session.println(f.cause().getMessage());
	            }
	        }
	    });
	}
}

class CommandJoin implements ChatCommand {
	private final String roomName;
	private final RoomRegister roomReg;
    private final Database db;
    private final EventExecutorGroup executor;
    private final Factory factory;
	
	CommandJoin(String roomName, Factory factory) {
        this.roomName = roomName;
        this.factory = factory;
        this.roomReg = factory.roomRegister();
        this.db = factory.database();
        this.executor = factory.executor();
	}
	
	@Override
	public void exec(Session session) {
	    Optional<Room> room = roomReg.find(roomName);
	    if (room.isPresent()) {
	        session.join(room.get());
	    } else {
	        Future<?> future = executor.submit(() -> {
	            Optional<Room> optionalRoom = roomReg.find(roomName);
	            Room oldOrNewRoom = optionalRoom.orElse(db.findOrCreateRoom(roomName, factory));
	            Room roomNew = roomReg.findOrProduce(roomName, r -> oldOrNewRoom);
	            session.join(roomNew);
	        });
	        future.addListener(f -> {
	            if (f.isDone() && !f.isSuccess()) {
	                if (f.isCancelled()) {
	                    session.println("Operation has been cancelled");
	                } else {
	                    session.println(f.cause().getMessage());
	                }
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
