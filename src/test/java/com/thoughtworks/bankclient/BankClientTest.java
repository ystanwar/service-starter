package com.thoughtworks.bankclient;


import com.thoughtworks.payment.BankDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BankClientTest {

    @Autowired
    BankClient bankClient;

    @Test
    public void checkBankDetails() throws Exception {
        BankDetails bankDetails = new BankDetails("user1", 12345, "HDFC1234");
        assertEquals(200, bankClient.checkBankDetails(bankDetails));

    }
}
