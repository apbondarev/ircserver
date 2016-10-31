package com.ab.ircserver;

public class WrongPasswordException extends IrcServerException {

    private static final long serialVersionUID = 8682388326917894232L;

    public WrongPasswordException() {
        
        super("Wrong password.");
    }
}
