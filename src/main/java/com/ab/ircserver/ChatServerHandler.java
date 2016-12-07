package com.ab.ircserver;

import java.util.Optional;

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
	    Session session = Session.current(ctx.channel());
	    cmd.exec(session);
	    
	    Optional<ChatCommand> longCmd = session.takeLongCommand();
	    if  (longCmd.isPresent()) {
	        ctx.fireChannelRead(longCmd.get());
	    } else {
	        ctx.fireChannelReadComplete();
	    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
