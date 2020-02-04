package com.thoughtworks.bankInfo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BankInfoServiceTest {

    @Autowired
    BankInfoService bankInfoService;

    @Autowired
    BankInfoRepository bankInfoRepository;

    @BeforeEach
    void tearDown() {
        bankInfoRepository.deleteAll();
    }

    @Test
    void createBankTest() throws BankInfoAlreadyExistsException {
        BankInfo bank = new BankInfo("HDFC", "http://localhost:8082");
        BankInfo savedBank = bankInfoService.create(bank);
        assertEquals(bank.getBankCode(), savedBank.getBankCode());
        assertEquals(bank.getUrl(), savedBank.getUrl());
    }

    @Test
    void cannotCreateBankWhenBankInfoAlreadyExists() throws BankInfoAlreadyExistsException {
        BankInfo bank = new BankInfo("HDFC", "http://localhost:8082");
        bankInfoService.create(bank);
        BankInfoAlreadyExistsException exception =
                assertThrows(BankInfoAlreadyExistsException.class, () -> bankInfoService.create(bank));

        assertEquals("Bank info already exists", exception.getValue());
    }

    @Test
    void cannotCreateBankWhenBankCodeIsNull() throws BankInfoAlreadyExistsException {
        assertThrows(IllegalArgumentException.class, () -> bankInfoService.create(new BankInfo(null, "http://localhost:8082")));
        assertThrows(IllegalArgumentException.class, () -> bankInfoService.create(new BankInfo("HDFC", null)));
        assertThrows(IllegalArgumentException.class, () -> bankInfoService.create(null));
        assertThrows(IllegalArgumentException.class, () -> bankInfoService.create(new BankInfo("", "http://localhost:8082")));
        assertThrows(IllegalArgumentException.class, () -> bankInfoService.create(new BankInfo("HDFC", "")));
    }



    @Test
    void fetchABankByBankCodeTest() throws BankInfoAlreadyExistsException {
        BankInfo bank = new BankInfo("HDFC", "http://localhost:8082");
        BankInfo savedBank = bankInfoService.create(bank);
        BankInfo fetchedBank = bankInfoService.fetchBankByBankCode("HDFC");
        assertEquals(savedBank.getBankCode(), fetchedBank.getBankCode());
        assertEquals(savedBank.getUrl(), fetchedBank.getUrl());
    }


    @Test
    void failsToFetchIfBankCodeIsNullOrEmpty(){
        assertThrows(IllegalArgumentException.class,()->bankInfoService.fetchBankByBankCode(null));
        assertThrows(IllegalArgumentException.class,()->bankInfoService.fetchBankByBankCode(""));
    }

    @Test
    void failsToFetchBankIfBankCodeIsMissing() throws BankInfoAlreadyExistsException {
        assertEquals(null,bankInfoService.fetchBankByBankCode("ICIC"));
    }


}
