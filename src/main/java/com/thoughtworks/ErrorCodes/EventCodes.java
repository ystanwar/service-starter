package com.thoughtworks.ErrorCodes;

public enum EventCodes {
    PAYMENT_SUCCESSFUL("payment request successful"),
    ACCOUNT_NOT_FOUND("account details are incorrect"),
    BANK_INFO_EXIST("bankinfo already exists"),
    SUSPECTED_ACCOUNT("suspected request"),
    INVALID_FIELD("invalid input passed"),
    INVALID_IFSC_FORMAT("invalid ifscCode format"),
    PAYMENT_REQUEST_NOT_READABLE("request body missing or incorrect format"),
    CALL_NOT_PERMITTED("could not process the request"),
    SERVER_UNREACHABLE("could not process the request"),
    FRAUD_SERVICE_UNAVAILABLE("fraud service unavailable"),
    BANK_INFO_NOT_FOUND("bank info not found"),
    BANK_SERVICE_UNAVAILABLE("bank service unavailable"),
    REQUEST_RECEIVED("request received"),
    RESPONSE_SENT("response sent");

    private final String description;

    EventCodes(String description) {
        this.description = description;
    }
}
