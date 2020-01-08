package com.thoughtworks.bank;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
public class Bank {
    @Id
    @ApiModelProperty(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String bankCode;

    @Column
    private String url;

    public int getId() {
        return id;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getUrl() {
        return url;
    }

    public Bank(String bankCode, String url) {
        this.bankCode = bankCode;
        this.url = url;
    }

    public Bank() {
    }
}
