package com.thoughtworks.payment;

public class PaymentRequest {
    private int amount;
    private BankDetails beneficiary;
    private BankDetails payee;

    int getAmount() {
        return amount;
    }

    BankDetails getBeneficiary() {
        return beneficiary;
    }

    BankDetails getPayee() {
        return payee;
    }

    PaymentRequest(int amount, BankDetails beneficiary, BankDetails payee) {
        this.amount = amount;
        this.beneficiary = beneficiary;
        this.payee = payee;
    }

}
