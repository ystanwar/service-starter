package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ResourceNotFoundException(String errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public ResourceNotFoundException(String errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public ResourceNotFoundException(String errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }

}
