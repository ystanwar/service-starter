package com.thoughtworks.exceptions;

import com.google.gson.JsonObject;
import com.thoughtworks.ErrorCodes.InternalErrorCodes;

public class PaymentRefusedException extends BusinessException {
    public PaymentRefusedException(InternalErrorCodes errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public PaymentRefusedException(InternalErrorCodes errorCode, String errorMessage, Exception causedByException) {
        super(errorCode, errorMessage, causedByException);
    }

    public PaymentRefusedException(InternalErrorCodes errorCode, String errorMessage, JsonObject details) {
        super(errorCode, errorMessage, details);
    }

    public PaymentRefusedException(InternalErrorCodes errorCode, String errorMessage, JsonObject details, Exception causedByException) {
        super(errorCode, errorMessage, details, causedByException);
    }
}
