package com.rjdiscbots.tftbot.exceptions.message;

public class InvalidMessageException extends Exception {

    public InvalidMessageException() {
        super();
    }

    public InvalidMessageException(String error) {
        super(error);
    }
}
