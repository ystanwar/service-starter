package com.thoughtworks.serviceclients;

import com.thoughtworks.BankClient.api.BankDetailsApi;
import com.thoughtworks.ErrorCodes.InternalErrorCodes;
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
import java.util.UUID;

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
            throw new ValidationException(InternalErrorCodes.INVALID_IFSC_FORMAT, "Invalid ifscCode format ->" + ifscCode);
        }
    }

    public boolean checkBankDetails(long accountNumber, String ifscCode) throws Exception {
        BankInfo bankInfo = bankService.fetchBankByBankCode(getBankCode(ifscCode));
        if (bankInfo == null) throw new ResourceNotFoundException(InternalErrorCodes.BANK_INFO_NOT_FOUND, "Bank info not found for " + ifscCode);
        baseUrl = bankInfo.getUrl();
        String xoutGoingRequestId = "";
        try {
            bankDetailsApi.getApiClient().setBasePath(baseUrl);
            bankDetailsApi.getApiClient().addDefaultHeader("X-Correlation-ID", MDC.get("X-Correlation-ID"));
            xoutGoingRequestId = String.valueOf(UUID.randomUUID());
            bankDetailsApi.getApiClient().addDefaultHeader("X-Request-ID", xoutGoingRequestId);
            bankDetailsApi.checkDetails(accountNumber, ifscCode);
            return true;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 404) {
                return false;
            } else {
                throw new DependencyException("ExternalService", InternalErrorCodes.SERVER_ERROR, baseUrl + "/checkDetails", "outgoing x-request-id : " + xoutGoingRequestId + " received " + ex.getStatusCode().value());
            }
        } catch (Exception ex) {
            throw new DependencyException("ExternalService", InternalErrorCodes.SERVER_ERROR, baseUrl + "/checkDetails", "UNAVAILABLE" + " for outgoing x-request-id : " + xoutGoingRequestId , ex);
        }
    }
}
