package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;

public class PaymentRefusedException extends BusinessException {
    public PaymentRefusedException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public PaymentRefusedException(String errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public PaymentRefusedException(String errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public PaymentRefusedException(String errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }
}
