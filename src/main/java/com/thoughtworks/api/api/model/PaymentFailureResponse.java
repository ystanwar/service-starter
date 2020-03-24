package com.thoughtworks.api.api.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * PaymentFailureResponse
 */

public class PaymentFailureResponse   {
  @JsonProperty("message")
  private String message;

  @JsonProperty("reasons")
  @Valid
  private Map<String, String> reasons = null;

  public PaymentFailureResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  @ApiModelProperty(value = "")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public PaymentFailureResponse reasons(Map<String, String> reasons) {
    this.reasons = reasons;
    return this;
  }

  public PaymentFailureResponse putReasonsItem(String key, String reasonsItem) {
    if (this.reasons == null) {
      this.reasons = new HashMap<>();
    }
    this.reasons.put(key, reasonsItem);
    return this;
  }

  /**
   * Get reasons
   * @return reasons
  */
  @ApiModelProperty(value = "")


  public Map<String, String> getReasons() {
    return reasons;
  }

  public void setReasons(Map<String, String> reasons) {
    this.reasons = reasons;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentFailureResponse paymentFailureResponse = (PaymentFailureResponse) o;
    return Objects.equals(this.message, paymentFailureResponse.message) &&
        Objects.equals(this.reasons, paymentFailureResponse.reasons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, reasons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentFailureResponse {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    reasons: ").append(toIndentedString(reasons)).append("\n");
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

