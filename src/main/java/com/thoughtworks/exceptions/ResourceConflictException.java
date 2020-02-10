package com.thoughtworks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends ServiceException {
    public ResourceConflictException(String key, String value) {
        super(key,value);
    }
    public ResourceConflictException(String key, String value, Exception causedByException) {
        super(key,value, causedByException);
    }

}
