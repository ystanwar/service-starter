package com.thoughtworks.payment;

import com.thoughtworks.payment.model.BankDetails;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Validated
public class PaymentRequest {
    @Max(value = 100000, message = "amount cannot be greater than 100000")
    private int amount;
    @NotNull(message = "beneficiary info cannot be null")
    @Valid
    private BankDetails beneficiary;
    @NotNull(message = "payee info cannot be null")
    @Valid
    private BankDetails payee;

    public PaymentRequest() {

    }

    public int getAmount() {
        return amount;
    }

    public BankDetails getBeneficiary() {
        return beneficiary;
    }

    public BankDetails getPayee() {
        return payee;
    }

    public PaymentRequest(int amount, BankDetails beneficiary, BankDetails payee) {
        this.amount = amount;
        this.beneficiary = beneficiary;
        this.payee = payee;
    }

}
