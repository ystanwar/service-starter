package com.thoughtworks.exceptions;

public class ValidationException extends ServiceException {

    public ValidationException(String key, String value) {
        super(key,value);
    }
    public ValidationException(String key, String value, Exception causedByException) {
        super(key,value, causedByException);
    }

}
