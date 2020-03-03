package com.thoughtworks.serviceclients;

import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.bankInfo.BankInfoService;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.exceptions.ValidationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Retry(name = " bankservice")
@CircuitBreaker(name = "bankservice")
public class BankClient {

    private String baseUrl;

    @Autowired
    BankInfoService bankService;

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
        String url = baseUrl + "/checkDetails";
        HttpGet get = buildUrl(url, accountNumber, ifscCode);

        int statusCode;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpclient.execute(get);
            statusCode = response.getStatusLine().getStatusCode();
            response.close();
        } catch (Exception ex) {
            throw new DependencyException("ExternalService", "BANKSERVICE_" + ifscCode, url, "UNAVAILABLE", ex);
            //throw new DependencyException("SERVICE_UNAVAILABLE ", "could not call " + getBankCode(ifscCode), ex);
        }

        if (statusCode == 200)
            return true;
        else if (statusCode == 404) {
            return false;
        } else {
            throw new DependencyException("ExternalService", "BANKSERVICE_" + ifscCode, url, "SERVICE_ERROR - " + statusCode);
            //throw new DependencyException("SERVICE_ERROR",  "calling " + ifscCode + " received statusCode=" + statusCode);
        }
    }

    private HttpGet buildUrl(String baseUrl, long accountNumber, String ifscCode) throws URISyntaxException {
        HttpGet get = new HttpGet(baseUrl);
        URI uri = new URIBuilder(get.getURI())
                .addParameter("accountNumber", String.valueOf(accountNumber))
                .addParameter("ifscCode", ifscCode)
                .build();

        get.setURI(uri);
        get.setHeader("PARENT_REQ_ID", MDC.get("request.id"));
        return get;
    }
}
