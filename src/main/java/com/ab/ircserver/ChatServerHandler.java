package com.ab.ircserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatCommand> {
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session.newSession(ctx.channel());
        super.channelActive(ctx);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatCommand cmd) throws Exception {
		Channel channel = ctx.channel();
		if (cmd.isLongRunning()) {
		    ctx.fireChannelRead(cmd);
		} else {
		    Session session = Session.current(channel);
		    cmd.exec(session);
		    ctx.fireChannelReadComplete();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
