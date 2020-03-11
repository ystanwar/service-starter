package com.thoughtworks.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Payment
 */

public class Payment   {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("amount")
  private Integer amount;

  @JsonProperty("beneficiaryName")
  private String beneficiaryName;

  @JsonProperty("beneficiaryAccountNumber")
  private Long beneficiaryAccountNumber;

  @JsonProperty("beneficiaryIfscCode")
  private String beneficiaryIfscCode;

  @JsonProperty("payeeName")
  private String payeeName;

  @JsonProperty("payeeAccountNumber")
  private Long payeeAccountNumber;

  @JsonProperty("payeeIfscCode")
  private String payeeIfscCode;

  @JsonProperty("status")
  private String status;

  @JsonProperty("requestId")
  private String requestId;

  public Payment id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Payment amount(Integer amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
  */
  @ApiModelProperty(value = "")


  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Payment beneficiaryName(String beneficiaryName) {
    this.beneficiaryName = beneficiaryName;
    return this;
  }

  /**
   * Get beneficiaryName
   * @return beneficiaryName
  */
  @ApiModelProperty(value = "")


  public String getBeneficiaryName() {
    return beneficiaryName;
  }

  public void setBeneficiaryName(String beneficiaryName) {
    this.beneficiaryName = beneficiaryName;
  }

  public Payment beneficiaryAccountNumber(Long beneficiaryAccountNumber) {
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    return this;
  }

  /**
   * Get beneficiaryAccountNumber
   * @return beneficiaryAccountNumber
  */
  @ApiModelProperty(value = "")


  public Long getBeneficiaryAccountNumber() {
    return beneficiaryAccountNumber;
  }

  public void setBeneficiaryAccountNumber(Long beneficiaryAccountNumber) {
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
  }

  public Payment beneficiaryIfscCode(String beneficiaryIfscCode) {
    this.beneficiaryIfscCode = beneficiaryIfscCode;
    return this;
  }

  /**
   * Get beneficiaryIfscCode
   * @return beneficiaryIfscCode
  */
  @ApiModelProperty(value = "")


  public String getBeneficiaryIfscCode() {
    return beneficiaryIfscCode;
  }

  public void setBeneficiaryIfscCode(String beneficiaryIfscCode) {
    this.beneficiaryIfscCode = beneficiaryIfscCode;
  }

  public Payment payeeName(String payeeName) {
    this.payeeName = payeeName;
    return this;
  }

  /**
   * Get payeeName
   * @return payeeName
  */
  @ApiModelProperty(value = "")


  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public Payment payeeAccountNumber(Long payeeAccountNumber) {
    this.payeeAccountNumber = payeeAccountNumber;
    return this;
  }

  /**
   * Get payeeAccountNumber
   * @return payeeAccountNumber
  */
  @ApiModelProperty(value = "")


  public Long getPayeeAccountNumber() {
    return payeeAccountNumber;
  }

  public void setPayeeAccountNumber(Long payeeAccountNumber) {
    this.payeeAccountNumber = payeeAccountNumber;
  }

  public Payment payeeIfscCode(String payeeIfscCode) {
    this.payeeIfscCode = payeeIfscCode;
    return this;
  }

  /**
   * Get payeeIfscCode
   * @return payeeIfscCode
  */
  @ApiModelProperty(value = "")


  public String getPayeeIfscCode() {
    return payeeIfscCode;
  }

  public void setPayeeIfscCode(String payeeIfscCode) {
    this.payeeIfscCode = payeeIfscCode;
  }

  public Payment status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @ApiModelProperty(value = "")


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Payment requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
  */
  @ApiModelProperty(value = "")


  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Payment payment = (Payment) o;
    return Objects.equals(this.id, payment.id) &&
        Objects.equals(this.amount, payment.amount) &&
        Objects.equals(this.beneficiaryName, payment.beneficiaryName) &&
        Objects.equals(this.beneficiaryAccountNumber, payment.beneficiaryAccountNumber) &&
        Objects.equals(this.beneficiaryIfscCode, payment.beneficiaryIfscCode) &&
        Objects.equals(this.payeeName, payment.payeeName) &&
        Objects.equals(this.payeeAccountNumber, payment.payeeAccountNumber) &&
        Objects.equals(this.payeeIfscCode, payment.payeeIfscCode) &&
        Objects.equals(this.status, payment.status) &&
        Objects.equals(this.requestId, payment.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, amount, beneficiaryName, beneficiaryAccountNumber, beneficiaryIfscCode, payeeName, payeeAccountNumber, payeeIfscCode, status, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Payment {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    beneficiaryName: ").append(toIndentedString(beneficiaryName)).append("\n");
    sb.append("    beneficiaryAccountNumber: ").append(toIndentedString(beneficiaryAccountNumber)).append("\n");
    sb.append("    beneficiaryIfscCode: ").append(toIndentedString(beneficiaryIfscCode)).append("\n");
    sb.append("    payeeName: ").append(toIndentedString(payeeName)).append("\n");
    sb.append("    payeeAccountNumber: ").append(toIndentedString(payeeAccountNumber)).append("\n");
    sb.append("    payeeIfscCode: ").append(toIndentedString(payeeIfscCode)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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

