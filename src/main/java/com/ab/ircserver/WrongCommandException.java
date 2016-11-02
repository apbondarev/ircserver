package com.ab.ircserver;

public class WrongCommandException extends ChatServerException {

    private static final long serialVersionUID = 8682388326917894232L;

    public WrongCommandException(String message) {
		super(message);
	}

	public WrongCommandException() {
        super("Wrong command.");
    }
}
