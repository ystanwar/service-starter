package com.thoughtworks.api.payment;

import java.util.Map;

public class PaymentFailureResponse {
    private String message;
    private Map<String, String> reasons;

    public PaymentFailureResponse(String message, Map<String, String> reasons) {
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
