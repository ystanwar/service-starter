package com.thoughtworks.serviceclients;

import com.thoughtworks.ErrorCodes.InternalErrorCodes;
import com.thoughtworks.FraudClient.api.CheckFraudApi;
import com.thoughtworks.FraudClient.model.PaymentDetailsRequest;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.payment.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class FraudClient {
    @Value("${FraudService.Url}")
    private String baseUrl;

    @Autowired
    CheckFraudApi checkFraudApi;

    public boolean checkFraud(Payment payment) throws Exception {
        PaymentDetailsRequest paymentDetailsRequest = new PaymentDetailsRequest();
        paymentDetailsRequest.setAmount(payment.getAmount());
        paymentDetailsRequest.setBeneficiaryName(payment.getBeneficiaryName());
        paymentDetailsRequest.setBeneficiaryAccountNumber(payment.getBeneficiaryAccountNumber());
        paymentDetailsRequest.setPayeeName(payment.getPayeeName());
        paymentDetailsRequest.setPayeeAccountNumber(payment.getPayeeAccountNumber());
        paymentDetailsRequest.setPayeeIfscCode(payment.getPayeeIfscCode());
        try {
            checkFraudApi.getApiClient().setBasePath(baseUrl);
            checkFraudApi.checkFraud(paymentDetailsRequest);
            log.info("FraudService url from config server", kv("eventCode", "FRAUDERVICE_URL_FROM_CONFIG_SERVER"), kv("url", baseUrl));
            return true;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 422) {
                return false;
            } else {
                throw new DependencyException("ExternalService", InternalErrorCodes.SERVER_ERROR, baseUrl + "/checkFraud", "received " + ex.getStatusCode().value());
            }
        } catch (Exception ex) {
            throw new DependencyException("ExternalService", InternalErrorCodes.SERVER_ERROR, baseUrl + "/checkFraud", "UNAVAILABLE", ex);
        }
    }
}
