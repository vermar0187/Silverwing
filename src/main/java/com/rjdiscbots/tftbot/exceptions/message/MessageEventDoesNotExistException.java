package com.rjdiscbots.tftbot.exceptions.message;

public class MessageEventDoesNotExistException extends Exception {

    public MessageEventDoesNotExistException() {
        super();
    }

    public MessageEventDoesNotExistException(String error) {
        super(error);
    }
}
