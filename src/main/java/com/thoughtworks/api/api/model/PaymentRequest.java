package com.thoughtworks.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * PaymentRequest
 */

public class PaymentRequest   {
  @JsonProperty("amount")
  private Integer amount;

  @JsonProperty("beneficiary")
  private BankDetails beneficiary;

  @JsonProperty("payee")
  private BankDetails payee;

  public PaymentRequest amount(Integer amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * maximum: 100000
   * @return amount
  */
  @ApiModelProperty(example = "100", value = "")

 @Max(100000) 
  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public PaymentRequest beneficiary(BankDetails beneficiary) {
    this.beneficiary = beneficiary;
    return this;
  }

  /**
   * Get beneficiary
   * @return beneficiary
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public BankDetails getBeneficiary() {
    return beneficiary;
  }

  public void setBeneficiary(BankDetails beneficiary) {
    this.beneficiary = beneficiary;
  }

  public PaymentRequest payee(BankDetails payee) {
    this.payee = payee;
    return this;
  }

  /**
   * Get payee
   * @return payee
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public BankDetails getPayee() {
    return payee;
  }

  public void setPayee(BankDetails payee) {
    this.payee = payee;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRequest paymentRequest = (PaymentRequest) o;
    return Objects.equals(this.amount, paymentRequest.amount) &&
        Objects.equals(this.beneficiary, paymentRequest.beneficiary) &&
        Objects.equals(this.payee, paymentRequest.payee);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, beneficiary, payee);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequest {\n");
    
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    beneficiary: ").append(toIndentedString(beneficiary)).append("\n");
    sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
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

