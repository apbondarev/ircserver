package com.ab.ircserver;

public enum ChatState {

	INITIAL {
		@Override
		public void login(Session session, CommandLogin cmd) {
		    cmd.exec(session);
		}

		@Override
		public void join(Session session, CommandJoin cmd) {
			session.println("Start with: /login name password");
		}

		@Override
		public void printUsers(Session session) {
			session.println("Start with: /login name password");
		}

		@Override
		public void sendMessage(Session session, Message msg) {
			session.println("Start with: /login name password");
		}
	},
	
	LOGGED_IN {
		@Override
		public void login(Session session, CommandLogin cmd) {
			session.println("You've logged in already as user '" + session.username() + "'");
		}

		@Override
		public void join(Session session, CommandJoin cmd) {
			cmd.exec(session);
		}

		@Override
		public void printUsers(Session session) {
			session.println("Join a channel: /join channel");
		}

		@Override
		public void sendMessage(Session session, Message msg) {
			session.println("Join a channel: /join channel");
		}
	},
	

	JOINED {
		@Override
		public void login(Session session, CommandLogin cmd) {
			session.println("You've logged in already as user '" + session.username() + "'");
		}

		@Override
		public void join(Session session, CommandJoin cmd) {
			cmd.exec(session);
		}

		@Override
		public void printUsers(Session session) {
			session.printUsers();
		}

		@Override
		public void sendMessage(Session session, Message msg) {
			session.sendInRoom(msg);
		}
	},
	
	DISCONNECTED;
	
	void login(Session session, CommandLogin cmd) {
		// do nothing
	}
	
	void join(Session session, CommandJoin cmd) {
		// do nothing
	}
	
	void leave(Session session) {
		session.leave();
	}

	void printUsers(Session session) {
		// do nothing
	}
	
	void sendMessage(Session session, Message msg) {
		// do nothing
	}
	
}
