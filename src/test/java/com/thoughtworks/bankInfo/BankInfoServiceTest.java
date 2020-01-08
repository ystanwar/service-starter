package com.thoughtworks.bankInfo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BankInfoServiceTest {

    @Autowired
    BankInfoService bankInfoService;

    @Autowired
    BankInfoRepository bankInfoRepository;

    @BeforeEach
    void tearDown() {
        System.out.println("teardown running...");
        bankInfoRepository.deleteAll();
    }

    @Test
    void createBankTest() {
        BankInfo bank = new BankInfo("HDFC", "http://localhost:8082");
        BankInfo savedBank = bankInfoService.create(bank);
        assertEquals(bank.getBankCode(), savedBank.getBankCode());
        assertEquals(bank.getUrl(), savedBank.getUrl());
    }

    @Test
    void fetchABankByBankCodeTest() {
        BankInfo bank = new BankInfo("HDFC", "http://localhost:8082");
        BankInfo savedBank = bankInfoService.create(bank);
        BankInfo fetchedBank = bankInfoService.fetchBankByBankCode("HDFC");
        assertEquals(savedBank.getBankCode(), fetchedBank.getBankCode());
        assertEquals(savedBank.getUrl(), fetchedBank.getUrl());
    }
}
