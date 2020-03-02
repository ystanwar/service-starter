package com.thoughtworks.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(example = "user", description = "")
    public String getName() {
        return name;
    }

    @Schema(example = "12345", description = "")
    public long getAccountNumber() {
        return accountNumber;
    }

    @Schema(example = "HDFC1234", description = "")
    public String getIfscCode() {
        return ifscCode;
    }
}
