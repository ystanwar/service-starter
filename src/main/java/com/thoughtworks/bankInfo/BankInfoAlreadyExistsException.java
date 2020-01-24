package com.thoughtworks.bankInfo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BankInfoAlreadyExistsException extends Exception {
    private final String key;
    private final String value;

    public BankInfoAlreadyExistsException(String key, String value) {
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
