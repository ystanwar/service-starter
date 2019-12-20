package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    @Test
    public void expectsPaymentAfterSerialization() throws IOException {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);
        ObjectMapper objectMapper = new ObjectMapper();
        String detailsString = objectMapper.writeValueAsString(payment);
        assertTrue(detailsString.contains("\"id\":0"));
        assertTrue(detailsString.contains("\"amount\":100"));
        assertTrue(detailsString.contains("\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}"));

        assertTrue(detailsString.contains("\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}"));
    }
}
