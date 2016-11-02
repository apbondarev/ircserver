package com.ab.ircserver;

public class StateDisconnected implements ChatState {
	
    public static final ChatState INSTANCE = new StateDisconnected();
    
    private StateDisconnected() {
        // Not used
    }

	@Override
	public ChatState login(Session session, String name, byte[] password) {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState join(Session session, String roomName) {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState leave(Session session) {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState printUsers(Session session) {
		throw new IrcServerException("Client disconnected");
	}

	@Override
	public ChatState sendMessage(Session session, Message msg) {
		throw new IrcServerException("Client disconnected");
	}

}
