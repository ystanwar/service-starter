package com.thoughtworks.payment;

public class Payment {
    private final int id;
    private final int amount;
    private final BankDetails beneficiary;
    private final BankDetails payee;

    public Payment(int id, int amount, BankDetails beneficiary, BankDetails payee) {
        this.id=id;
        this.amount=amount;
        this.beneficiary=beneficiary;
        this.payee=payee;
    }

    public int getId() {
        return id;
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
}
