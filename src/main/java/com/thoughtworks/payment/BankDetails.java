package com.thoughtworks.payment;

public class BankDetails {
    private String name;
    private long accountNumber;
    private String ifscCode;

    public BankDetails(String name, long accountNumber, String ifscCode) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
    }

    public BankDetails() {
    }

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
