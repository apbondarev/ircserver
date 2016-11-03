package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatCommand> {

	private Session session;
	private ChatState state;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		session = Session.anonimous(ctx.channel());
		state = ChatState.INITIAL;
		session.println("You are connected to IRC server.");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand cmd) throws Exception {
		state = cmd.exec(session, state);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
