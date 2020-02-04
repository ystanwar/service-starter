package com.thoughtworks.bankclient;

import com.thoughtworks.bankInfo.BankInfo;

public class BankInfoNotFoundException extends Exception{
    BankInfoNotFoundException(String message){
        super(message);
    }
}
