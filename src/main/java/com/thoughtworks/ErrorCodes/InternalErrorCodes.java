package com.thoughtworks.ErrorCodes;

public enum InternalErrorCodes {
    ACCOUNT_NOT_FOUND("404", "account details are incorrect"),
    BANK_INFO_EXIST("409", "bankinfo already exists"),
    SUSPECTED_ACCOUNT("422", "suspected request"),
    INVALID_FIELD("400", "invalid input passed"),
    INVALID_IFSC_FORMAT("400", "invalid ifscCode format"),
    PAYMENT_REQUEST_NOT_READABLE("400", "request body missing or incorrect format"),
    CALL_NOT_PERMITTED("500", "could not process the request"),
    BANK_INFO_NOT_FOUND("404", "bank info not found"),
    SERVER_ERROR("500", "could not process the request"),
    SYSTEM_ERROR("500", "system error");

    private final String code;
    private final String description;

    InternalErrorCodes(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
