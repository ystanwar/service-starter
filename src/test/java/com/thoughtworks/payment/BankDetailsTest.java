package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BankDetailsTest {

    @Test
    public void expectsNoteWithTitleAndDescriptionAfterSerialization() throws IOException {
        BankDetails details = new BankDetails(1, "user1", 12345, "HDFC1234");
        ObjectMapper objectMapper = new ObjectMapper();

        String detailsString = objectMapper.writeValueAsString(details);

        assertTrue(detailsString.contains("\"name\":\"user1\""));
        assertTrue(detailsString.contains("\"ifscCode\":\"HDFC1234\""));
        assertTrue(detailsString.contains("\"accountNumber\":12345"));
    }
}
