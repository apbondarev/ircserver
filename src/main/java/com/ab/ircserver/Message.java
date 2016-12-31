package com.ab.ircserver;

public class Message {

	private final String username;
	private final String text;

	public Message(String username, String text) {
		super();
		this.username = username;
		this.text = text;
	}

	public String username() {
		return username;
	}

	public String text() {
		return text;
	}

}
