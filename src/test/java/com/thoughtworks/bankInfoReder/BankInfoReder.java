package com.thoughtworks.bankInfoReder;

import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.bankInfoSeed.BankInfoProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankInfoReder {
//    @Autowired
//    BankInfoProcessor bankInfoProcessor;

    @Test
    void createBankTest() throws IOException {
        BankInfoProcessor bankInfoProcessor=new BankInfoProcessor();

        BankInfo savedBank = bankInfoProcessor.processBankInfoData();
        assertEquals("", savedBank.getBankCode());

    }
}
