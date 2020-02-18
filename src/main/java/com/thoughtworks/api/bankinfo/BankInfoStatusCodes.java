package com.thoughtworks.api.bankinfo;

public enum BankInfoStatusCodes {
    CREATED(201,"bankinfo is created"),
    CONFLICT(409,"bankinfo already exists"),
    INTERNAL_SERVER_ERROR(500,"could not process the request"),
    BAD_REQUEST(400,"invalid input");

    private final int code;
    private final String description;

    BankInfoStatusCodes(int code, String description){
        this.code=code;
        this.description=description;
    }
}
