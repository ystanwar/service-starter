package com.thoughtworks.api.api.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * BankDetails
 */

public class BankDetails   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("accountNumber")
  private Long accountNumber;

  @JsonProperty("ifscCode")
  private String ifscCode;

  public BankDetails name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(example = "user", required = true, value = "")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BankDetails accountNumber(Long accountNumber) {
    this.accountNumber = accountNumber;
    return this;
  }

  /**
   * Get accountNumber
   * @return accountNumber
  */
  @ApiModelProperty(example = "12345", required = true, value = "")
  @NotNull


  public Long getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(Long accountNumber) {
    this.accountNumber = accountNumber;
  }

  public BankDetails ifscCode(String ifscCode) {
    this.ifscCode = ifscCode;
    return this;
  }

  /**
   * Get ifscCode
   * @return ifscCode
  */
  @ApiModelProperty(example = "HDFC1234", required = true, value = "")
  @NotNull


  public String getIfscCode() {
    return ifscCode;
  }

  public void setIfscCode(String ifscCode) {
    this.ifscCode = ifscCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BankDetails bankDetails = (BankDetails) o;
    return Objects.equals(this.name, bankDetails.name) &&
        Objects.equals(this.accountNumber, bankDetails.accountNumber) &&
        Objects.equals(this.ifscCode, bankDetails.ifscCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, accountNumber, ifscCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BankDetails {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    accountNumber: ").append(toIndentedString(accountNumber)).append("\n");
    sb.append("    ifscCode: ").append(toIndentedString(ifscCode)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

