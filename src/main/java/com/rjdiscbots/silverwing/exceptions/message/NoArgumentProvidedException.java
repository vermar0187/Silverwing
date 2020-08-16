package com.rjdiscbots.silverwing.exceptions.message;

public class NoArgumentProvidedException extends InvalidMessageException {

    public NoArgumentProvidedException() {
        super();
    }

    public NoArgumentProvidedException(String error) {
        super(error);
    }
}
