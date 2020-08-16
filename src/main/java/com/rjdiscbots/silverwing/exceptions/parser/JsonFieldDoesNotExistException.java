package com.rjdiscbots.silverwing.exceptions.parser;

public class JsonFieldDoesNotExistException extends PatchProcessingException {

    public JsonFieldDoesNotExistException() {
        super();
    }

    public JsonFieldDoesNotExistException(String error) {
        super(error);
    }
}
