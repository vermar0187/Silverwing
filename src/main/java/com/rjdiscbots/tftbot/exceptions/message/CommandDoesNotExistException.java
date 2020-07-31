package com.rjdiscbots.tftbot.exceptions.message;

public class CommandDoesNotExistException extends InvalidMessageException {

    public CommandDoesNotExistException() {
        super();
    }

    public CommandDoesNotExistException(String error) {
        super(error);
    }
}
