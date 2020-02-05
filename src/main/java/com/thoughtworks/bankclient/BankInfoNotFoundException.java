package com.thoughtworks.bankclient;

public class BankInfoNotFoundException extends Exception{
    BankInfoNotFoundException(String message){
        super(message);
    }
}
