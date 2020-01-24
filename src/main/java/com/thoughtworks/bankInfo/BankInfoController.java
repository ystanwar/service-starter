package com.thoughtworks.bankInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankinfo")
public class BankInfoController {

    @Autowired
    BankInfoService bankInfoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankInfo create(@RequestBody BankInfo bankInfo) throws BankInfoAlreadyExistsException {
        return bankInfoService.create(bankInfo);
    }

}
