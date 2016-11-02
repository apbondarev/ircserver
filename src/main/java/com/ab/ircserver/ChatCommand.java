package com.ab.ircserver;

/**
 * Interface for chat commands.
 * @author albondarev
 */
@FunctionalInterface
public interface ChatCommand {

	ChatState exec(Session session, ChatState state);

}
