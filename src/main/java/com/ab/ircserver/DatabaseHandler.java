package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DatabaseHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	private static Database db = new InMemoryDatabase();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand msg) throws Exception {
		Session session = Session.current(ctx.channel());
		
		if (msg instanceof CommandLogin) {
			CommandLogin cmd = (CommandLogin) msg;
			User user = db.findOrCreateUser(cmd.userName(), cmd.password());
			session.login(user, cmd.password());
		} else if (msg instanceof CommandJoin) {
			CommandJoin cmd = (CommandJoin) msg;
			Room room = db.findOrCreateRoom(cmd.roomName());
			session.join(room);
		}
		ctx.fireChannelRead(msg);
	}

}
