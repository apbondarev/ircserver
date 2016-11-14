package com.ab.ircserver;

import java.time.LocalDateTime;

public class Message {

	private final String username;
	private final LocalDateTime when;
	private final String text;

	public Message(String username, String text) {
		super();
		this.username = username;
		this.when = LocalDateTime.now();
		this.text = text;
	}

	public String username() {
		return username;
	}

	public LocalDateTime when() {
		return when;
	}

	public String text() {
		return text;
	}

}
