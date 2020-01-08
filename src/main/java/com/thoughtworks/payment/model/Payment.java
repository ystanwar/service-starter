package com.thoughtworks.payment.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thoughtworks.payment.PaymentDeSerializer;
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

    @Column(name = "bene_name")
    private String beneficiaryName;

    @Column(name = "bene_acc_num")
    private long beneficiaryAccountNumber;

    @Column(name = "bene_ifsc")
    private String beneficiaryIfscCode;

    @Column(name = "payee_name")
    private String payeeName;

    @Column(name = "payee_acc_num")
    private long payeeAccountNumber;

    @Column(name = "payee_ifsc")
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
