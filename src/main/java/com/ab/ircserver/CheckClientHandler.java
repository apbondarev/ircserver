package com.ab.ircserver;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class CheckClientHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush("/ping\r\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Session session = Session.current(ctx.channel());
        session.println("Error: " + cause.getMessage());
        CommandLeave.INSTANCE.exec(session);
    }

}
