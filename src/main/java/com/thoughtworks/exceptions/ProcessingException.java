package com.thoughtworks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ProcessingException extends ServiceException {
    public ProcessingException(String key, String value) {
        super(key,value);
    }
    public ProcessingException(String key, String value, Exception causedByException) {
        super(key,value, causedByException);
    }
}
