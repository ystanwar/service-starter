package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.payment.model.BankDetails;
import com.thoughtworks.payment.model.Payment;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentTest {

    @Test
    public void expectsPaymentAfterSerialization() throws IOException {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);
        payment.setRequestId("payment1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String detailsString = objectMapper.writeValueAsString(payment);
        assertTrue(detailsString.contains("\"id\":0"));
        assertTrue(detailsString.contains("\"amount\":1"));
        assertTrue(detailsString.contains("\"beneficiaryName\":\"user1\""));
        assertTrue(detailsString.contains("\"beneficiaryAccountNumber\":12345"));
        assertTrue(detailsString.contains("\"beneficiaryIfscCode\":\"HDFC1234\""));
        assertTrue(detailsString.contains("\"payeeName\":\"user2\""));
        assertTrue(detailsString.contains("\"payeeAccountNumber\":12346"));
        assertTrue(detailsString.contains("\"payeeIfscCode\":\"HDFC1234\""));
        assertTrue(detailsString.contains("\"requestId\":\"payment1234\""));

    }

    //   @Test
//    void expectsPaymentWithNestingAfterDeSerialization() throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String paymentString = "{\"amount\":500," +
//                "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
//                ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
//                "}";
//
//        Payment payment = objectMapper.readValue(paymentString, Payment.class);
//
//        assertEquals(500, payment.getAmount());
//        assertEquals("user1", payment.getBeneficiaryName());
//        assertEquals(12345, payment.getBeneficiaryAccountNumber());
//        assertEquals("HDFC1234", payment.getBeneficiaryIfscCode());
//        assertEquals("user2", payment.getPayeeName());
//        assertEquals(12346, payment.getPayeeAccountNumber());
//        assertEquals("HDFC1234", payment.getPayeeIfscCode());
//    }
    @Test
    void expectsPaymentWithoutNestingAfterDeSerialization() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String paymentString = "{\"amount\":500," +
                "\"beneficiaryName\":\"user1\",\"beneficiaryAccountNumber\":12345,\"beneficiaryIfscCode\":\"HDFC1234\"" +
                ",\"payeeName\":\"user2\",\"payeeAccountNumber\":12346,\"payeeIfscCode\":\"HDFC1234\"" +
                ",\"requestId\":\"payment1234\"}";

        Payment payment = objectMapper.readValue(paymentString, Payment.class);

        assertEquals(500, payment.getAmount());
        assertEquals("user1", payment.getBeneficiaryName());
        assertEquals(12345, payment.getBeneficiaryAccountNumber());
        assertEquals("HDFC1234", payment.getBeneficiaryIfscCode());
        assertEquals("user2", payment.getPayeeName());
        assertEquals(12346, payment.getPayeeAccountNumber());
        assertEquals("HDFC1234", payment.getPayeeIfscCode());
        assertEquals("payment1234", payment.getRequestId());
    }
}
