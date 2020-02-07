package com.thoughtworks.exceptions;

public class ValidationException extends Exception {
    private final String key;
    private final String value;

    public ValidationException(String key, String value) {
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
