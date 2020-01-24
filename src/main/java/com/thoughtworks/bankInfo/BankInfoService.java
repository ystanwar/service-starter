package com.thoughtworks.bankInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankInfoService {

    @Autowired
    BankInfoRepository bankInfoRepository;

    public BankInfo create(BankInfo bank) throws BankInfoAlreadyExistsException {
        BankInfo fetchedBankInfo=fetchBankByBankCode(bank.getBankCode());
        if(fetchedBankInfo!=null) {
            throw new BankInfoAlreadyExistsException("message","Bank info already exists");
        }

        return bankInfoRepository.save(bank);
    }

    public BankInfo fetchBankByBankCode(String bankCode) {
        return bankInfoRepository.findByBankCode(bankCode);
    }

}
