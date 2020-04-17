package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;
import com.thoughtworks.ErrorCodes.InternalErrorCodes;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(InternalErrorCodes errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ResourceNotFoundException(InternalErrorCodes errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public ResourceNotFoundException(InternalErrorCodes errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public ResourceNotFoundException(InternalErrorCodes errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }

}
