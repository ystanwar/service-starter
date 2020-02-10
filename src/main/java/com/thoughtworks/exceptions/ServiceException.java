package com.thoughtworks.exceptions;

public class ServiceException extends Exception{
    private final String key;
    private final String value;

    public ServiceException(String key, String value, Exception causedByException) {
        super(value,causedByException);
        this.key = key;
        this.value = value;

    }
    public ServiceException(String key, String value) {
        super(value);
        this.key = key;
        this.value = value;

    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
