package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    @Test
    public void expectsPaymentAfterSerialization() throws IOException {
        BankDetails beneficiary = new BankDetails(1, "user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails(1, "user2", 12346, "HDFC1234");

        Payment payment = new Payment(1, 100, beneficiary, payee);
        ObjectMapper objectMapper = new ObjectMapper();

        String detailsString = objectMapper.writeValueAsString(payment);

        assertTrue(detailsString.contains("\"id\":1"));
        assertTrue(detailsString.contains("\"amount\":100"));
        assertTrue(detailsString.contains("\"beneficiary_accountNumber\":12345"));
        assertTrue(detailsString.contains("\"payee_accountNumber\":12346"));
        assertTrue(detailsString.contains("\"beneficiary_ifscCode\":\"HDFC1234\""));
        assertTrue(detailsString.contains("\"payee_ifscCode\":\"HDFC1234\""));
        assertTrue(detailsString.contains("\"payee_name\":\"user2\""));
        assertTrue(detailsString.contains("\"beneficiary_name\":\"user1\""));
    }
}
