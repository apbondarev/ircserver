package com.ab.ircserver;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import io.netty.channel.Channel;

interface ChatState {
	
	void login(Session session, User user, byte[] password);
	
	void join(Session session, RoomContext newRoomCtx);
	
	void printUsers(Session session);
	
	void sendMessage(Session session, String text);
	
	void leave(Session session);
	
	Optional<User> user();
}

class Initial implements ChatState {

	@Override
	public void login(Session session, User user, byte[] password) {
		if (!user.isCorrectPassword(password)) {
			session.println("Wrong password");
			return;
		}
		
		session.setState(new LoggedIn(user));
		session.println("Welcome " + user.name() + "!");
	}

	@Override
	public void join(Session session, RoomContext newRoomCtx) {
		session.println("Start with: /login name password");
	}

	@Override
	public void printUsers(Session session) {
		session.println("Start with: /login name password");
	}

	@Override
	public void sendMessage(Session session, String text) {
		session.println("Start with: /login name password");
	}
	
	@Override
	public void leave(Session session) {
		session.setState(new Disconnected());
		session.close("Disconnect.\r\n");
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
	public void login(Session session, User user, byte[] password) {
		session.println("You've logged in already as user '" + user.name() + "'");
	}

	@Override
	public void join(Session session, RoomContext newRoomCtx) {
	    Room newRoom = newRoomCtx.room();
		if (newRoom == Room.UNDEFINED) {
			session.println("Wrong channel '" + newRoomCtx.name() + "'.");
		} else {
		    BiConsumer<Boolean, Throwable> action = (result, exception) -> {
		        if (exception != null) {
		            exception.printStackTrace();
		        } else if (result) {
        		    newRoomCtx.notifyMessage("User '" + user.name() + "' has joined the channel '" + newRoomCtx.name() + "'");
        			session.setState(new Joined(user, newRoomCtx));
        			List<Message> lastMessages = newRoom.lastMessages();
        			session.send(lastMessages);
        		} else {
        			session.println("Max " + Room.CAPACITY + " active clients per channel is allowed.");
        		}
		    };
		    CompletionStage<Boolean> future = newRoomCtx.addSession(session);
		    future.whenComplete(action);
		}
	}

	@Override
	public void printUsers(Session session) {
		session.println("Join a channel: /join channel");
	}

	@Override
	public void sendMessage(Session session, String text) {
		session.println("Join a channel: /join channel");
	}
	
	@Override
	public void leave(Session session) {
		session.setState(new Disconnected());
		session.close("Disconnect.\r\n");
	}
	
	@Override
    public Optional<User> user() {
        return Optional.of(user);
    }
}
	
class Joined implements ChatState {

	private final User user;
	
	private final RoomContext roomCtx;
	
	Joined(User user, RoomContext roomCtx) {
		this.user = user;
		this.roomCtx = roomCtx;
	}
	
	@Override
	public void login(Session session, User user, byte[] password) {
		session.println("You've logged in already as user '" + user.name() + "'");
	}

	@Override
	public void join(Session session, RoomContext newRoomCtx) {
	    Room newRoom = newRoomCtx.room();
		if (newRoomCtx.room() == Room.UNDEFINED) {
			session.println("Wrong channel '" + user.name() + "'.");
			return;
		} else {
		    BiConsumer<Boolean, Throwable> action = (result, exception) -> {
		        if (exception != null) {
                    exception.printStackTrace();
                } else if (result) {
		            roomCtx.removeSession(session);
		            roomCtx.notifyMessage("User '" + user.name() + "' has left the channel '" + roomCtx.name() + "'");
		            newRoomCtx.notifyMessage("User '" + user.name() + "' has joined the channel '" + newRoom.name() + "'");
		            session.setState(new Joined(user, newRoomCtx));
		            List<Message> lastMessages = newRoom.lastMessages();
		            session.send(lastMessages);
		            return;
		        } else {
		            session.println("Max " + Room.CAPACITY + " active clients per channel is allowed.");
		            return;
		        }
		    };
		    CompletionStage<Boolean> future = newRoomCtx.addSession(session);
            future.whenComplete(action);
		}
	}

	@Override
	public void printUsers(Session session) {
	    Channel channel = session.channel();
	    roomCtx.users().forEach(u -> channel.write(u + "\r\n"));
        channel.flush();
	}

	@Override
	public void sendMessage(Session session, String text) {
		Message msg = new Message(user.name(), text);
		roomCtx.send(msg);
	}
	
	@Override
	public void leave(Session session) {
	    session.setState(new Disconnected());
	    roomCtx.removeSession(session);
	    roomCtx.notifyMessage("User '" + user.name() + "' has left the channel '" + roomCtx.name() + "'");
		session.close("Disconnect.\r\n");
	}
	
	@Override
    public Optional<User> user() {
        return Optional.of(user);
    }
}
	
class Disconnected implements ChatState {
	
	public void login(Session session, User user, byte[] password) {
		// do nothing
	}
	
	public void join(Session session, RoomContext newRoomCtx) {
		// do nothing
	}
	
	public void leave(Session session) {
		// do nothing
	}

	public void printUsers(Session session) {
		// do nothing
	}
	
	public void sendMessage(Session session, String text) {
		// do nothing
	}
	
	@Override
    public Optional<User> user() {
        return Optional.empty();
    }
}
