package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BankDetailsTest {

    @Test
    public void expectsNoteWithTitleAndDescriptionAfterSerialization() throws IOException {
        BankDetails note = new BankDetails("user1", 12345, "HDFC1234");
        ObjectMapper objectMapper = new ObjectMapper();

        String noteString = objectMapper.writeValueAsString(note);

        Assertions.assertTrue(noteString.contains("\"name\":\"user1\""));
        Assertions.assertTrue(noteString.contains("\"ifscCode\":\"HDFC1234\""));
        Assertions.assertTrue(noteString.contains("\"accountNumber\":12345"));
    }
}
