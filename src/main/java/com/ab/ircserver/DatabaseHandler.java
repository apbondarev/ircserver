package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DatabaseHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	private static Database db = new InMemoryDatabase();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand msg) throws Exception {
		Session session = ctx.channel().attr(ChatServerHandler.KEY_SESSION).get();
		
		if (msg instanceof CommandLogin) {
			CommandLogin cmd = (CommandLogin) msg;
			User user = db.findOrCreateUser(cmd.userName(), cmd.password());
			if (session.login(user, cmd.password())) {
				 ctx.channel().attr(ChatServerHandler.KEY_STATE).set(ChatState.LOGGED_IN);
			}
		} else if (msg instanceof CommandJoin) {
			CommandJoin cmd = (CommandJoin) msg;
			Room room = db.findOrCreateRoom(cmd.roomName());
			if (session.join(room)) {
				ctx.channel().attr(ChatServerHandler.KEY_STATE).set(ChatState.JOINED);
			}
		}
		ctx.fireChannelRead(msg);
	}

}
