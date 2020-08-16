package com.rjdiscbots.silverwing.exceptions.message;

public class EntityDoesNotExistException extends InvalidMessageException {

    public EntityDoesNotExistException() {
        super();
    }

    public EntityDoesNotExistException(String error) {
        super(error);
    }
}
