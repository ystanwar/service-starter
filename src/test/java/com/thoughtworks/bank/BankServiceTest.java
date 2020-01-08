package com.thoughtworks.bank;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BankServiceTest {

    @Autowired
    BankService bankService;

    @Autowired
    BankRepository bankRepository;

    @BeforeEach
    void tearDown() {
        System.out.println("teardown running...");
        bankRepository.deleteAll();
    }

    @Test
    void createBankTest() {
        Bank bank = new Bank("HDFC", "http://localhost:8082");
        Bank savedBank = bankService.create(bank);
        assertEquals(bank.getBankCode(), savedBank.getBankCode());
        assertEquals(bank.getUrl(), savedBank.getUrl());
    }

    @Test
    void fetchABankByBankCodeTest() {
        Bank bank = new Bank("HDFC", "http://localhost:8082");
        Bank savedBank = bankService.create(bank);
        Bank fetchedBank = bankService.fetchBankByBankCode("HDFC");
        assertEquals(savedBank.getBankCode(), fetchedBank.getBankCode());
        assertEquals(savedBank.getUrl(), fetchedBank.getUrl());
    }
}
