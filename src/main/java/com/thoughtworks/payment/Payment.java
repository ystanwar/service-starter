package com.thoughtworks.payment;

import javax.persistence.*;

@Entity
public class Payment {
    @Id
    private int id;
    private int amount;

    private String beneficiary_name;
    private long beneficiary_accountNumber;
    private String beneficiary_ifscCode;

    private String payee_name;
    private long payee_accountNumber;
    private String payee_ifscCode;

    public Payment() {
    }

    public Payment(int id, int amount, BankDetails beneficiary, BankDetails payee) {
        this.id = id;
        this.amount = amount;
        this.beneficiary_name = beneficiary.getName();
        this.beneficiary_accountNumber = beneficiary.getAccountNumber();
        this.beneficiary_ifscCode = beneficiary.getIfscCode();
        this.payee_name = payee.getName();
        this.payee_accountNumber = payee.getAccountNumber();
        this.payee_ifscCode = payee.getIfscCode();
    }


    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getBeneficiary_name() {
        return beneficiary_name;
    }

    public long getBeneficiary_accountNumber() {
        return beneficiary_accountNumber;
    }

    public String getBeneficiary_ifscCode() {
        return beneficiary_ifscCode;
    }

    public String getPayee_name() {
        return payee_name;
    }

    public long getPayee_accountNumber() {
        return payee_accountNumber;
    }

    public String getPayee_ifscCode() {
        return payee_ifscCode;
    }
}
