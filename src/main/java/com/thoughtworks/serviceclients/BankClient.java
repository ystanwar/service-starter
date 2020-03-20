package com.thoughtworks.serviceclients;

import com.thoughtworks.BankClient.api.BankDetailsApi;
import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.bankInfo.BankInfoService;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.exceptions.ValidationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Retry(name = " bankservice")
@CircuitBreaker(name = "bankservice")
public class BankClient {

    private String baseUrl;

    @Autowired
    BankInfoService bankService;

    @Autowired
    BankDetailsApi bankDetailsApi;

    private String getBankCode(String ifscCode) throws ValidationException {
        if (ifscCode != null && ifscCode.length() >= 5) {
            return ifscCode.substring(0, 4);
        } else {
            throw new ValidationException("message", "Invalid ifscCode format ->" + ifscCode);
        }
    }

    public boolean checkBankDetails(long accountNumber, String ifscCode) throws Exception {
        BankInfo bankInfo = bankService.fetchBankByBankCode(getBankCode(ifscCode));
        if (bankInfo == null) throw new ResourceNotFoundException("message", "Bank info not found for " + ifscCode);
        baseUrl = bankInfo.getUrl();
        try {
            bankDetailsApi.getApiClient().setBasePath(baseUrl);
            bankDetailsApi.getApiClient().addDefaultHeader("PARENT_REQ_ID", MDC.get("request.id"));
            bankDetailsApi.checkDetails(accountNumber, ifscCode);
            return true;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 404) {
                return false;
            } else {
                throw new DependencyException("ExternalService", "BANKSERVICE_" + ifscCode, baseUrl + "/checkDetails", "received " + ex.getStatusCode().value());
            }
        } catch (Exception ex) {
            throw new DependencyException("ExternalService", "BANKSERVICE_" + ifscCode, baseUrl + "/checkDetails", "UNAVAILABLE", ex);
        }
    }
}
