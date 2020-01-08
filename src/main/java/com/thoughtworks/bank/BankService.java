package com.thoughtworks.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    @Autowired
    BankRepository bankRepository;

    public Bank create(Bank bank) {
        return bankRepository.save(bank);
    }
}
