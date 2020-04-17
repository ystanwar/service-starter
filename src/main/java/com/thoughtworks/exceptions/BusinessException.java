package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;
import com.thoughtworks.ErrorCodes.InternalErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessException extends ServiceException {
    public BusinessException(InternalErrorCodes errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public BusinessException(InternalErrorCodes errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public BusinessException(InternalErrorCodes errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public BusinessException(InternalErrorCodes errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }
}
