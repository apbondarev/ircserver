package com.ab.ircserver;

public class IrcServerException extends RuntimeException {

    private static final long serialVersionUID = 285352651346349233L;

    public IrcServerException() {
        super();
    }

    public IrcServerException(String message) {
        super(message);
    }

    public IrcServerException(Throwable cause) {
        super(cause);
    }

    public IrcServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IrcServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
