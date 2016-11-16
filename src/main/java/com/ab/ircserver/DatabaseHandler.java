package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DatabaseHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	private final Database db;
	
	public DatabaseHandler(Database db) {
        this.db = db;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand msg) throws Exception {
		Session session = Session.current(ctx.channel());
		ChatState state = session.state();
		
		if (msg instanceof CommandLogin) {
			CommandLogin cmd = (CommandLogin) msg;
			User user = db.findOrCreateUser(cmd.userName(), cmd.password());
			state.login(session, user, cmd);
		} else if (msg instanceof CommandJoin) {
			CommandJoin cmd = (CommandJoin) msg;
			Room room = db.findOrCreateRoom(cmd.roomName());
			state.join(session, room);
		}
		ctx.fireChannelRead(msg);
	}

}
