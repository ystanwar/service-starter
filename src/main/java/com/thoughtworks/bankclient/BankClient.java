package com.thoughtworks.bankclient;

import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.bankInfo.BankInfoService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class BankClient {

    private String baseUrl;

    @Autowired
    BankInfoService bankService;

    private String getBankCode(String ifscCode) throws InvalidIfscCodeFormatException {
        if (ifscCode!=null&&ifscCode.length() >= 5) {
            return ifscCode.substring(0, 4);
        } else {
            throw new InvalidIfscCodeFormatException(ifscCode);
        }
    }

    public boolean checkBankDetails(long accountNumber, String ifscCode) throws Exception {

        BankInfo bankInfo = bankService.fetchBankByBankCode(getBankCode(ifscCode));
        if (bankInfo == null) throw new BankInfoNotFoundException("Bank info not found for " + ifscCode);
        baseUrl = bankInfo.getUrl();
        String url = baseUrl + "/checkDetails";
        HttpGet get = buildUrl(url, accountNumber, ifscCode);

        int statusCode = 0;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(get);
        statusCode = response.getStatusLine().getStatusCode();
        response.close();

        if (statusCode == 200)
            return true;
        else if (statusCode == 404) {
            return false;
        } else {
            throw new Exception("Error calling bank service for " + ifscCode + "; received statusCode=" + statusCode);
        }
    }

    private HttpGet buildUrl(String baseUrl, long accountNumber, String ifscCode) throws URISyntaxException {
        HttpGet get = new HttpGet(baseUrl);
        URI uri = new URIBuilder(get.getURI())
                .addParameter("accountNumber", String.valueOf(accountNumber))
                .addParameter("ifscCode", ifscCode)
                .build();
        get.setURI(uri);
        return get;
    }
}
