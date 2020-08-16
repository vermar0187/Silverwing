package com.rjdiscbots.silverwing.exceptions.message;

public class InvalidMessageException extends Exception {

    public InvalidMessageException() {
        super();
    }

    public InvalidMessageException(String error) {
        super(error);
    }
}
