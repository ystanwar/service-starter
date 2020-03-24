package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.api.api.model.BankDetails;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BankDetailsTest {

    @Test
    public void expectsBankDetailsAfterSerialization() throws IOException {
        BankDetails details = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        ObjectMapper objectMapper = new ObjectMapper();

        String detailsString = objectMapper.writeValueAsString(details);

        assertTrue(detailsString.contains("\"name\":\"user1\""));
        assertTrue(detailsString.contains("\"ifscCode\":\"HDFC1234\""));
        assertTrue(detailsString.contains("\"accountNumber\":12345"));
    }
}
