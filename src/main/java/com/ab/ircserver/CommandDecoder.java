package com.ab.ircserver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class CommandDecoder extends MessageToMessageDecoder<String> {

	private static final String LOGIN = "/login";
	private static final String JOIN = "/join";
	private static final String LEAVE = "/leave";
	private static final String USERS = "/users";

	private static final Pattern SPACE = Pattern.compile("(\\w+)\\s*(.*)");

	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		String commandStr = msg.trim();
		if (commandStr.startsWith(LOGIN)) {
			String argStr = commandStr.substring(LOGIN.length()).trim();
			Matcher matcher = SPACE.matcher(argStr);
			if (matcher.matches()) {
				String name = matcher.group(1);
				byte[] password = matcher.group(2).getBytes(StandardCharsets.UTF_8);
				out.add(new CommandLogin(name, password));
			} else {
				out.add(new CommandWrong("Wrong command. Expected: /login name password"));
			}
		} else if (commandStr.startsWith(JOIN)) {
			String roomName = commandStr.substring(JOIN.length()).trim();
			out.add(new CommandJoin(roomName));
		} else if (commandStr.startsWith(LEAVE)) {
			out.add(new CommandLeave());
		} else if (commandStr.startsWith(USERS)) {
			out.add(new CommandUsers());
		} else {
			out.add(new CommandMessage(commandStr));
		}
	}

}
