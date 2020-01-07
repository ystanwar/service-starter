package com.thoughtworks.payment;

public class PaymentRequest {
    private int amount;
    private BankDetails beneficiary;
    private BankDetails payee;
    public PaymentRequest(){

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
