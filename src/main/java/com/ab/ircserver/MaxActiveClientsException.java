package com.ab.ircserver;

public class MaxActiveClientsException extends ChatServerException {

    private static final long serialVersionUID = 8682388326917894232L;

    public MaxActiveClientsException(String message) {
		super(message);
	}

	public MaxActiveClientsException() {
        super("Max active clients per channel is exceeded.");
    }
}
