package com.thoughtworks.payment;

import javax.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;

    @OneToOne
    @JoinColumn(name = "Beneficiary_Id")
    private BankDetails beneficiary;

    @OneToOne
    @JoinColumn(name = "Payee_Id")
    private BankDetails payee;

    public Payment() {
    }

    public Payment(int amount, BankDetails beneficiary, BankDetails payee) {
        this.amount = amount;
        this.beneficiary = beneficiary;
        this.payee = payee;
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

    public void setId(int id) {
        this.id = id;
    }
}
