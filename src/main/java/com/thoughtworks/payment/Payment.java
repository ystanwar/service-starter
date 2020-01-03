package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@JsonDeserialize(using = PaymentDeSerializer.class)
@Entity
public class Payment {
    @Id
    @ApiModelProperty(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;

    @JoinColumn(name = "bene_name")
    private String beneficiaryName;

    @JoinColumn(name = "bene_acc_num")
    private long beneficiaryAccountNumber;

    @JoinColumn(name = "bene_ifsc")
    private String beneficiaryIfscCode;

    @JoinColumn(name = "payee_name")
    private String payeeName;

    @JoinColumn(name = "payee_acc_num")
    private long payeeAccountNumber;

    @JoinColumn(name = "payee_ifsc")
    private String payeeIfscCode;

    public Payment() {
    }

    public Payment(int amount, BankDetails beneficiary, BankDetails payee) {
        this.amount = amount;
        this.beneficiaryName = beneficiary.getName();
        this.beneficiaryAccountNumber = beneficiary.getAccountNumber();
        this.beneficiaryIfscCode = beneficiary.getIfscCode();
        this.payeeName = payee.getName();
        this.payeeAccountNumber = payee.getAccountNumber();
        this.payeeIfscCode = payee.getIfscCode();
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public long getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public String getBeneficiaryIfscCode() {
        return beneficiaryIfscCode;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public long getPayeeAccountNumber() {
        return payeeAccountNumber;
    }

    public String getPayeeIfscCode() {
        return payeeIfscCode;
    }

    public void setId(int id) {
        this.id = id;
    }
}
