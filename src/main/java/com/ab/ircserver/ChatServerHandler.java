package com.ab.ircserver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    
    private static final String LOGIN = "/login ";
    private static final String JOIN = "/join ";
    private static final String LEAVE = "/leave ";
    private static final String USERS = "/users ";
    
    public static final Pattern SPACE = Pattern.compile("(\\a)+\\s*(.)");
    
    private UserRegister userRegister;
    private RoomRegister roomRegister;
    private Session session;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String commandStr = (String) msg;
            commandStr = commandStr.trim();
            process(ctx, commandStr);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void process(ChannelHandlerContext ctx, String commandStr) {
        if (commandStr.startsWith(LOGIN)) {
            String argStr = commandStr.substring(LOGIN.length());
            Matcher matcher = SPACE.matcher(argStr);
            if (!matcher.matches()) {
                ctx.write("Wrong command. Expected: /login name password");
                ctx.flush();
            }
            String name = matcher.group(1);
            byte[] password = matcher.group(2).getBytes(StandardCharsets.UTF_8);
            
            User user;
            try {
                user = userRegister.login(name, password);
                session = new Session(user, ctx.channel());
            } catch (WrongPasswordException e) {
                ctx.write("Wrong password.");
                ctx.flush();
            }
        } else if (commandStr.startsWith(JOIN)) {
            String roomName = commandStr.substring(JOIN.length());
            Room room = roomRegister.findOrCreate(roomName);
            session.join(room);
            List<Message> lastMessages = room.lastMessages();
            lastMessages.forEach(m -> ctx.write(m.text()));
            ctx.flush();
        } else if (commandStr.startsWith(LEAVE)) {
            session.leave();
            ctx.write("Bye!");
            ctx.flush();
            ctx.channel().close();
        } else if (commandStr.startsWith(USERS)) {
            session.room().users().forEach(u -> ctx.write(u + "\n"));
            ctx.flush();
        } else {
            
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
