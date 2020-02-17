package com.thoughtworks.exceptions;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(String key, String value) {
        super(key, value);
    }

    public ResourceNotFoundException(String key, String value, Exception causedByException) {
        super(key, value, causedByException);
    }

}
