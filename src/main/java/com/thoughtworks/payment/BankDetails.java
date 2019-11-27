package com.thoughtworks.payment;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BankDetails {

    @Id
    private int id;
    private String name;
    private long accountNumber;
    private String ifscCode;

    public BankDetails(int id,String name, int accountNumber, String ifscCode) {
        this.id=id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
    }

    public BankDetails(){}

    public String getName() {
        return name;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }
}
