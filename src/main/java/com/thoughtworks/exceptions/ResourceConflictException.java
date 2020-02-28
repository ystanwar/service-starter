package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends ServiceException {
    public ResourceConflictException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ResourceConflictException(String errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public ResourceConflictException(String errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public ResourceConflictException(String errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }


}
