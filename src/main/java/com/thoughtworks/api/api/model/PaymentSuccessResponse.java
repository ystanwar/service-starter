package com.thoughtworks.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * PaymentSuccessResponse
 */

public class PaymentSuccessResponse   {
  @JsonProperty("statusMessage")
  private String statusMessage;

  @JsonProperty("paymentId")
  private Integer paymentId;

  public PaymentSuccessResponse statusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
    return this;
  }

  /**
   * Get statusMessage
   * @return statusMessage
  */
  @ApiModelProperty(value = "")


  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public PaymentSuccessResponse paymentId(Integer paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
  */
  @ApiModelProperty(value = "")


  public Integer getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Integer paymentId) {
    this.paymentId = paymentId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentSuccessResponse paymentSuccessResponse = (PaymentSuccessResponse) o;
    return Objects.equals(this.statusMessage, paymentSuccessResponse.statusMessage) &&
        Objects.equals(this.paymentId, paymentSuccessResponse.paymentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statusMessage, paymentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentSuccessResponse {\n");
    
    sb.append("    statusMessage: ").append(toIndentedString(statusMessage)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
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

