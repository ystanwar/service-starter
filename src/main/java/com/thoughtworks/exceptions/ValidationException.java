package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;

public class ValidationException extends ServiceException {

    public ValidationException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ValidationException(String errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public ValidationException(String errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public ValidationException(String errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }
}
