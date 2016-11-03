package com.ab.ircserver;

public enum ChatState {

	INITIAL {
		@Override
		public ChatState login(Session session, CommandLogin cmd) {
			if (session.login(cmd.userName(), cmd.password())) {
				return LOGGED_IN;
			} else {
				return this;
			}
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
			if (session.join(cmd.roomName())) {
				return JOINED;
			} else {
				return this;
			}
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
	
	DISCONNECTED;
	
	ChatState login(Session session, CommandLogin cmd) {
		return this;
	}
	
	ChatState join(Session session, CommandJoin cmd) {
		return this;
	}
	
	ChatState leave(Session session) {
		session.leave();
		return ChatState.DISCONNECTED;
	}

	ChatState printUsers(Session session) {
		return this;
	}
	
	ChatState sendMessage(Session session, Message msg) {
		return this;
	}
	
}
