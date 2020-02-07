package com.thoughtworks.messages;

import java.util.Map;

public class RequestFailureResponse {
    private String message;
    private Map<String, String> reasons;

    public RequestFailureResponse(String message, Map<String, String> reasons) {
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
