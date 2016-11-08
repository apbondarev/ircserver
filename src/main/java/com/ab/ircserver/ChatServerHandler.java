package com.ab.ircserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	static final AttributeKey<Session> KEY_SESSION = AttributeKey.valueOf("session");
	static final AttributeKey<ChatState> KEY_STATE = AttributeKey.valueOf("state");

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Attribute<Session> attrSession = ctx.channel().attr(KEY_SESSION);
		Session session = Session.anonimous(ctx.channel());
		attrSession.set(session);
		
		Attribute<ChatState> attrState = ctx.channel().attr(KEY_STATE);
		attrState.set(ChatState.INITIAL);
		
		session.println("You are connected to IRC server.");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand cmd) throws Exception {
		Session session = ctx.channel().attr(KEY_SESSION).get();
		
		Attribute<ChatState> attrState = ctx.channel().attr(KEY_STATE);
		ChatState state = attrState.get();
		state = cmd.exec(session, state);
		attrState.set(state);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
