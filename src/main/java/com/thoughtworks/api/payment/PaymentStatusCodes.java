package com.thoughtworks.api.payment;

public enum PaymentStatusCodes {

    OK(200,"payment request successful"),
    CREATED(201,"payment is created"),
    NOT_FOUND(404,"account details are incorrect"),
    INTERNAL_SERVER_ERROR(500,"could not process the request"),
    BAD_REQUEST(400,"invalid input");

    private final int code;
    private final String description;

    PaymentStatusCodes(int code, String description){
        this.code=code;
        this.description=description;
    }
}
