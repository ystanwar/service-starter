package com.thoughtworks.error;

import java.util.Map;

public class ValidationErrorResponse {
    private String message;
    private Map<String, String> reasons;

    public Map<String, String> getReasons() {
        return reasons;
    }

    public ValidationErrorResponse(String message, Map<String, String> reasons) {
        this.message = message;
        this.reasons = reasons;

    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ValidationErrorResponse{" +
                "message='" + message + '\'' +
                ", reasons=" + reasons +
                '}';
    }
}
