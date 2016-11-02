package com.ab.ircserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public interface ChatState {

	ChatState login(Session session, String name, byte[] password);
	
	ChatState join(Session session, String roomName);
	
	default ChatState leave(Session session) {
		ChannelFuture future = session.leave();
		future.addListener(ChannelFutureListener.CLOSE);
		return StateDisconnected.INSTANCE;
	}

	ChatState printUsers(Session session);
	
	ChatState sendMessage(Session session, Message msg);
	
}
