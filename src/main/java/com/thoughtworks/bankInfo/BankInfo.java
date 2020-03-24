package com.thoughtworks.bankInfo;

//import io.swagger.annotations.ApiModelProperty;
//import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.*;

@Entity
public class BankInfo {
    @Id
//    @ApiModelProperty(hidden = true)
    //@Schema(hidden = true)
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

    public BankInfo(String bankCode, String url) {
        this.bankCode = bankCode;
        this.url = url;
    }

    public BankInfo() {
    }
}
