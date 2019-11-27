package com.thoughtworks.payment;

public class BankDetails {
    private String name;
    private long accountNumber;
    private String ifscCode;

    public BankDetails(String name, int accountNumber, String ifscCode) {
        this.name=name;
        this.accountNumber=accountNumber;
        this.ifscCode=ifscCode;
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
