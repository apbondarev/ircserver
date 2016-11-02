package com.ab.ircserver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

	private static final String LOGIN = "/login";
	private static final String JOIN = "/join";
	private static final String LEAVE = "/leave";
	private static final String USERS = "/users";

	public static final Pattern SPACE = Pattern.compile("(\\w+)\\s*(.*)");

	private static UserRegister userRegister = new UserRegister();
	private static RoomRegister roomRegister = new RoomRegister();
	private Session session;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		session = Session.anonimous(ctx.channel());
		ctx.channel().writeAndFlush("You are connected to IRC server.\r\n");
	}

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
			String argStr = commandStr.substring(LOGIN.length()).trim();
			Matcher matcher = SPACE.matcher(argStr);
			if (!matcher.matches()) {
				session.println("Wrong command. Expected: /login name password");
				return;
			}
			String name = matcher.group(1);
			byte[] password = matcher.group(2).getBytes(StandardCharsets.UTF_8);

			try {
				User user = userRegister.login(name, password);
				session = new Session(user, ctx.channel());
				session.printWelcome();
			} catch (WrongPasswordException e) {
				session.println("Wrong password.");
				return;
			}
		} else if (commandStr.startsWith(JOIN)) {
			if (session.isAnonimous()) {
				session.println("Start with: /login name password");
				return;
			}
			String roomName = commandStr.substring(JOIN.length()).trim();
			Room room = roomRegister.findOrCreate(roomName);
			session.join(room);
			List<Message> lastMessages = room.lastMessages();
			lastMessages.forEach(session::send);
			session.flush();
		} else if (commandStr.startsWith(LEAVE)) {
			ChannelFuture future = session.leave();
			future.addListener(ChannelFutureListener.CLOSE);
			session = null;
		} else if (commandStr.startsWith(USERS)) {
			if (session.isAnonimous()) {
				session.println("Start with: /login name password");
				return;
			} else if (!session.inRoom()) {
				session.println("/join channel");
				return;
			}
			session.printUsers();
		} else {
			if (session.isAnonimous()) {
				session.println("Start with: /login name password");
				return;
			} else if (!session.inRoom()) {
				session.println("/join channel");
				return;
			}
			Message msg = new Message(session.user(), commandStr);
			session.sendInRoom(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
