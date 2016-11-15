package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session.newSession(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand cmd) throws Exception {
		Session session = Session.current(ctx.channel());
		cmd.exec(session);
		ctx.fireChannelRead(cmd);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
