package com.thoughtworks.api;

import org.springframework.stereotype.Component;

@Component
public class PaymentSuccessResponse {
    private String statusMessage;
    private int paymentId;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
}