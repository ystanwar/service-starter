package com.thoughtworks.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PayeeAccountDetailsNotFound extends Exception {
    private final String key;
    private final String value;

    public PayeeAccountDetailsNotFound(String key, String value) {
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
