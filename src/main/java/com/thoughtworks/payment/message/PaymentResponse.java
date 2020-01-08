package com.thoughtworks.payment.message;
import org.springframework.stereotype.Component;

@Component
public class PaymentResponse {
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
