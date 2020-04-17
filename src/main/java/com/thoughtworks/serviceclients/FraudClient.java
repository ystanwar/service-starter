package com.thoughtworks.serviceclients;

import com.thoughtworks.ErrorCodes.InternalErrorCodes;
import com.thoughtworks.FraudClient.api.CheckFraudApi;
import com.thoughtworks.FraudClient.model.PaymentDetailsRequest;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.payment.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class FraudClient {
    private String baseUrl;

    @Autowired
    FraudClient(Environment env) {
        this.baseUrl = env.getProperty("fraudService");
    }

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
