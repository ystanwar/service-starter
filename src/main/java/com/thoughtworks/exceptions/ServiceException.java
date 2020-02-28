package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;

public class ServiceException extends Exception {
    private final String errorCode;
    private final String errorMessage;
    private JsonObject details;


    public ServiceException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public ServiceException(String errorCode, String errorMessage, JsonObject details) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.details = details;

    }

    public ServiceException(String errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorMessage, causedByException);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.details = details;

    }

    public ServiceException(String errorCode, String errorMessage, Exception causedByException) {
        super(errorMessage, causedByException);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public JsonObject getDetails() {
        return details;
    }

}
