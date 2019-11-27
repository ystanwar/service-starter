package com.thoughtworks.payment;

public class BankDetails {
    private int id;
    private String name;
    private long accountNumber;
    private String ifscCode;

    public BankDetails(int id, String name, int accountNumber, String ifscCode) {
        this.id = id;
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
