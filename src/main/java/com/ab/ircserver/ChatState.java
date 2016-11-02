package com.ab.ircserver;

public enum ChatState {

	INITIAL {
		@Override
		public ChatState login(Session session, CommandLogin cmd) {
			session.login(cmd.userName(), cmd.password());
			return LOGGED_IN;
		}

		@Override
		public ChatState join(Session session, CommandJoin cmd) {
			session.println("Start with: /login name password");
			return this;
		}

		@Override
		public ChatState printUsers(Session session) {
			session.println("Start with: /login name password");
			return this;
		}

		@Override
		public ChatState sendMessage(Session session, Message msg) {
			session.println("Start with: /login name password");
			return this;
		}
	},
	
	LOGGED_IN {
		@Override
		public ChatState login(Session session, CommandLogin cmd) {
			session.println("You've logged in already as user '" + session.user().name() + "'");
			return this;
		}

		@Override
		public ChatState join(Session session, CommandJoin cmd) {
			session.join(cmd.roomName());
	        return JOINED;
		}

		@Override
		public ChatState printUsers(Session session) {
			session.println("Join a channel: /join channel");
			return this;
		}

		@Override
		public ChatState sendMessage(Session session, Message msg) {
			session.println("Join a channel: /join channel");
			return this;
		}
	},
	

	JOINED {
		@Override
		public ChatState login(Session session, CommandLogin cmd) {
			session.println("You've logged in already as user '" + session.user().name() + "'");
			return this;
		}

		@Override
		public ChatState join(Session session, CommandJoin cmd) {
			session.join(cmd.roomName());
			return this;
		}

		@Override
		public ChatState printUsers(Session session) {
			session.printUsers();
			return this;
		}

		@Override
		public ChatState sendMessage(Session session, Message msg) {
			session.sendInRoom(msg);
			return this;
		}
	},
	
	DISCONNECTED {
		@Override
		public ChatState login(Session session, CommandLogin cmd) {
			throw new ChatServerException("Client disconnected");
		}

		@Override
		public ChatState join(Session session, CommandJoin cmd) {
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
	};
	
	ChatState login(Session session, CommandLogin cmd) {
		throw new ChatServerException("Wrong command");
	}
	
	ChatState join(Session session, CommandJoin cmd) {
		throw new ChatServerException("Wrong command");
	}
	
	ChatState leave(Session session) {
		session.leave();
		return ChatState.DISCONNECTED;
	}

	ChatState printUsers(Session session) {
		throw new ChatServerException("Wrong command");
	}
	
	ChatState sendMessage(Session session, Message msg) {
		throw new ChatServerException("Wrong command");
	}
	
}
