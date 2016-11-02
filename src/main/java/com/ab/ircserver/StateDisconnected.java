package com.ab.ircserver;

public class StateDisconnected implements ChatState {
	
    public static final ChatState INSTANCE = new StateDisconnected();
    
    private StateDisconnected() {
        // Not used
    }

	@Override
	public ChatState login(Session session, CommandLogin cmd) {
		throw new ChatServerException("Client disconnected");
	}

	@Override
	public ChatState join(Session session, CommandJoin cmd) {
		throw new ChatServerException("Client disconnected");
	}

	@Override
	public ChatState leave(Session session) {
		throw new ChatServerException("Client disconnected");
	}

	@Override
	public ChatState printUsers(Session session) {
		throw new ChatServerException("Client disconnected");
	}

	@Override
	public ChatState sendMessage(Session session, Message msg) {
		throw new ChatServerException("Client disconnected");
	}

}
