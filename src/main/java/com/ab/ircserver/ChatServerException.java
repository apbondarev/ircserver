package com.ab.ircserver;

public class ChatServerException extends RuntimeException {

    private static final long serialVersionUID = 285352651346349233L;

    public ChatServerException() {
        super();
    }

    public ChatServerException(String message) {
        super(message);
    }

    public ChatServerException(Throwable cause) {
        super(cause);
    }

    public ChatServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
