package com.thoughtworks.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeneficiaryAccountDetailsNotFound extends Exception {
    public BeneficiaryAccountDetailsNotFound(String message) {
        super(message);
    }
}
