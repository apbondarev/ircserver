package com.ab.ircserver;

import com.ab.ircserver.ChatCommand.Result;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session.newSession(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand cmd) throws Exception {
		Channel channel = ctx.channel();
        Session session = Session.current(channel);
		Result result = cmd.exec(session);
		switch (result) {
		case COMPLETED:
		    ctx.fireChannelReadComplete();
		    break;
		case IN_DATABASE:
		    ctx.fireChannelRead(cmd);
		    break;
	    default:
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
