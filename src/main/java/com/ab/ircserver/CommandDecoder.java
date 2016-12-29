package com.ab.ircserver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.concurrent.EventExecutorGroup;

public class CommandDecoder extends MessageToMessageDecoder<String> {

	private static final String LOGIN = "/login";
	private static final String JOIN = "/join";
	private static final String LEAVE = "/leave";
	private static final String USERS = "/users";

	private static final Pattern SPACE = Pattern.compile("(\\w+)\\s*(.*)");
	
	private final Database db;
    private final RoomRegister roomReg;
    private final EventExecutorGroup executorGroup;

	public CommandDecoder(Database db, RoomRegister roomReg, EventExecutorGroup executorGroup) {
        super();
        this.db = db;
        this.roomReg = roomReg;
        this.executorGroup = executorGroup;
    }

    @Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		String commandStr = msg.trim();
		if (commandStr.startsWith(LOGIN)) {
			String argStr = commandStr.substring(LOGIN.length()).trim();
			Matcher matcher = SPACE.matcher(argStr);
			if (matcher.matches()) {
				String name = matcher.group(1);
				byte[] password = matcher.group(2).getBytes(StandardCharsets.UTF_8);
				out.add(new CommandLogin(name, password, db, executorGroup));
			} else {
				out.add(CommandWrong.LOGIN);
			}
		} else if (commandStr.startsWith(JOIN)) {
			String roomName = commandStr.substring(JOIN.length()).trim();
			out.add(new CommandJoin(roomName, roomReg, db, executorGroup));
		} else if (commandStr.startsWith(LEAVE)) {
			out.add(CommandLeave.INSTANCE);
		} else if (commandStr.startsWith(USERS)) {
			out.add(CommandUsers.INSTANCE);
		} else {
			out.add(new CommandMessage(commandStr));
		}
	}

}
