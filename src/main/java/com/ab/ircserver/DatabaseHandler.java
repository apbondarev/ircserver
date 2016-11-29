package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DatabaseHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand cmd) throws Exception {
		Session session = Session.current(ctx.channel());
		cmd.exec(session);
		ctx.fireChannelRead(cmd);
	}

}
