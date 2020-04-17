package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;
import com.thoughtworks.ErrorCodes.InternalErrorCodes;

public class ValidationException extends ServiceException {

    public ValidationException(InternalErrorCodes errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ValidationException(InternalErrorCodes errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public ValidationException(InternalErrorCodes errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public ValidationException(InternalErrorCodes errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }
}
