package com.rjdiscbots.tftbot.exceptions.message;

public class ListCommandDoesNotExistException extends InvalidMessageException {

    public ListCommandDoesNotExistException() {
        super();
    }

    public ListCommandDoesNotExistException(String error) {
        super(error);
    }
}
