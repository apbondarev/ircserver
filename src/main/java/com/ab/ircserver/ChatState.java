package com.ab.ircserver;

import java.util.List;
import java.util.Optional;

interface ChatState {
	
	void login(Session session, User user, CommandLogin cmd);
	
	void join(Session session, Room newRoom);
	
	void printUsers(Session session);
	
	void sendMessage(Session session, CommandMessage cmd);
	
	void leave(Session session);
	
	Optional<User> user();
}

class Initial implements ChatState {

	@Override
	public void login(Session session, User user, CommandLogin cmd) {
		if (!user.isCorrectPassword(cmd.password())) {
			session.println("Wrong password");
			return;
		}
		
		session.setState(new LoggedIn(user));
		session.println("Welcome " + user.name() + "!");
	}

	@Override
	public void join(Session session, Room newRoom) {
		session.println("Start with: /login name password");
	}

	@Override
	public void printUsers(Session session) {
		session.println("Start with: /login name password");
	}

	@Override
	public void sendMessage(Session session, CommandMessage cmd) {
		session.println("Start with: /login name password");
	}
	
	@Override
	public void leave(Session session) {
		session.setState(new Disconnected());
		session.close("Have a good day!\r\n");
	}

    @Override
    public Optional<User> user() {
        return Optional.empty();
    }
}
	
class LoggedIn implements ChatState {
	
	private final User user;
	
	LoggedIn(User user) {
		this.user = user;
	}
	
	@Override
	public void login(Session session, User user, CommandLogin cmd) {
		session.println("You've logged in already as user '" + user.name() + "'");
	}

	@Override
	public void join(Session session, Room newRoom) {
		if (newRoom == Room.UNDEFINED) {
			session.println("Wrong channel '" + newRoom.name() + "'.");
		} else if (newRoom.addSession(session)) {
			newRoom.notifyMessage("User '" + user.name() + "' has joined the channel '" + newRoom.name() + "'");
			session.setState(new Joined(user, newRoom));
			List<Message> lastMessages = newRoom.lastMessages();
			session.send(lastMessages);
		} else {
			session.println("Max " + Room.CAPACITY + " active clients per channel is allowed.");
		}
	}

	@Override
	public void printUsers(Session session) {
		session.println("Join a channel: /join channel");
	}

	@Override
	public void sendMessage(Session session, CommandMessage cmd) {
		session.println("Join a channel: /join channel");
	}
	
	@Override
	public void leave(Session session) {
		session.setState(new Disconnected());
		session.close("Have a good day!\r\n");
	}
	
	@Override
    public Optional<User> user() {
        return Optional.of(user);
    }
}
	
class Joined implements ChatState {

	private final User user;
	
	private final Room room;
	
	Joined(User user, Room room) {
		this.user = user;
		this.room = room;
	}
	
	@Override
	public void login(Session session, User user, CommandLogin cmd) {
		session.println("You've logged in already as user '" + user.name() + "'");
	}

	@Override
	public void join(Session session, Room newRoom) {
		if (newRoom == Room.UNDEFINED) {
			session.println("Wrong channel '" + user.name() + "'.");
			return;
		} else if (newRoom.addSession(session)) {
			room.removeSession(session);
			room.notifyMessage("User '" + user.name() + "' has left the channel '" + room.name() + "'");
			newRoom.notifyMessage("User '" + user.name() + "' has joined the channel '" + newRoom.name() + "'");
			session.setState(new Joined(user, newRoom));
			List<Message> lastMessages = newRoom.lastMessages();
			session.send(lastMessages);
			return;
		} else {
			session.println("Max " + Room.CAPACITY + " active clients per channel is allowed.");
			return;
		}
	}

	@Override
	public void printUsers(Session session) {
		session.println(room.users().stream());
	}

	@Override
	public void sendMessage(Session session, CommandMessage cmd) {
		Message msg = new Message(user.name(), cmd.text());
		room.send(msg);
	}
	
	@Override
	public void leave(Session session) {
		room.removeSession(session);
		room.notifyMessage("User '" + user.name() + "' has left the channel '" + room.name() + "'");
		session.setState(new Disconnected());
		session.close("Have a good day!\r\n");
	}
	
	@Override
    public Optional<User> user() {
        return Optional.of(user);
    }
}
	
class Disconnected implements ChatState {
	
	public void login(Session session, User user, CommandLogin cmd) {
		// do nothing
	}
	
	public void join(Session session, Room newRoom) {
		// do nothing
	}
	
	public void leave(Session session) {
		// do nothing
	}

	public void printUsers(Session session) {
		// do nothing
	}
	
	public void sendMessage(Session session, CommandMessage cmd) {
		// do nothing
	}
	
	@Override
    public Optional<User> user() {
        return Optional.empty();
    }
}
