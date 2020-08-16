package com.rjdiscbots.silverwing.exceptions.message;

public class MessageEventDoesNotExistException extends Exception {

    public MessageEventDoesNotExistException() {
        super();
    }

    public MessageEventDoesNotExistException(String error) {
        super(error);
    }
}
