package com.thoughtworks.error;

import java.util.Map;

public class PaymentErrorResponse {
    private String message;
    private Map<String, String> reasons;

    public PaymentErrorResponse(String message, Map<String, String> reasons) {
        this.message = message;
        this.reasons = reasons;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getReasons() {
        return reasons;
    }

    @Override
    public String toString() {
        return "{" +
                "message='" + message + '\'' +
                ", reasons=" + reasons +
                '}';
    }
}
