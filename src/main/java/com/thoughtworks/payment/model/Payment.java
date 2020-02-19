package com.thoughtworks.payment.model;

import io.swagger.annotations.ApiModelProperty;
import org.slf4j.MDC;

import javax.persistence.*;

//@JsonDeserialize(using = PaymentDeSerializer.class)
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


    @Column(name = "status")
    private String status;

    @Column(name="request_id")
    private String requestId;


    public Payment() {
    }

    public Payment(int amount, BankDetails beneficiary, BankDetails payee) {

        if (payee == null) {
            throw new IllegalArgumentException("payee cannot be null");
        } else if (amount <= 0) {
            throw new IllegalArgumentException("amount should be greater than zero");
        }else if( beneficiary == null){
            throw new IllegalArgumentException("beneficiary cannot be null");
        }

        this.amount = amount;
        this.beneficiaryName = beneficiary.getName();
        this.beneficiaryAccountNumber = beneficiary.getAccountNumber();
        this.beneficiaryIfscCode = beneficiary.getIfscCode();
        this.payeeName = payee.getName();
        this.payeeAccountNumber = payee.getAccountNumber();
        this.payeeIfscCode = payee.getIfscCode();
        this.status = "success";
        this.requestId= MDC.get("request.id");
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

    public String getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
