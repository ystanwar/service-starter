package com.thoughtworks.bankInfo;

import com.thoughtworks.ErrorCodes.InternalErrorCodes;
import com.thoughtworks.exceptions.ResourceConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankInfoService {

    @Autowired
    BankInfoRepository bankInfoRepository;

    public BankInfo create(BankInfo bank) throws ResourceConflictException {
        if (bank == null) throw new IllegalArgumentException("Bank info cannot be null");
        if (bank.getUrl() == null || bank.getUrl().length() == 0) {
            throw new IllegalArgumentException("Bank url cannot be null or empty");
        }
        BankInfo fetchedBankInfo = fetchBankByBankCode(bank.getBankCode());
        if (fetchedBankInfo != null) {
            throw new ResourceConflictException(InternalErrorCodes.BANK_INFO_EXIST, "Bank info already exists");
        }
        return bankInfoRepository.save(bank);
    }

    public BankInfo fetchBankByBankCode(String bankCode) {
        if (bankCode == null || bankCode.equals("")) {
            throw new IllegalArgumentException("bankcode should not be empty and null");
        }
        return bankInfoRepository.findByBankCode(bankCode);
    }
}
