package com.ab.ircserver;

import java.time.LocalDateTime;

public class Message {

	private final User from;
	private final LocalDateTime when;
	private final String text;

	public Message(User from, String text) {
		super();
		this.from = from;
		this.when = LocalDateTime.now();
		this.text = text;
	}

	public User from() {
		return from;
	}

	public LocalDateTime when() {
		return when;
	}

	public String text() {
		return text;
	}

}