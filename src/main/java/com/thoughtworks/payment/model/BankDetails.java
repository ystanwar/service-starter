package com.thoughtworks.payment.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BankDetails {
    @NotNull(message = "name must not be null")
    @NotEmpty(message = "name must not be empty")
    private String name;
    @NotNull(message = "account number must not be null")
    private long accountNumber;
    @NotNull(message = "ifsc code must not be null")
    @NotEmpty(message = "ifsc code must not be empty")
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
