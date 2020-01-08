package com.thoughtworks.bankclient;

import com.thoughtworks.bankInfo.BankInfoService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class BankClient {

    private String baseUrl;

    @Autowired
    BankInfoService bankService;

    private String getBankCode(String ifscCode) {
        return ifscCode.substring(0, 4);
    }

    public int checkBankDetails(long accountNumber, String ifscCode) throws IOException, URISyntaxException {
        baseUrl = bankService.fetchBankByBankCode(getBankCode(ifscCode)).getUrl();
        String url = baseUrl + "/checkDetails";
        HttpGet get = buildUrl(accountNumber, ifscCode, url);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        response.close();
        return statusCode;
    }

    private HttpGet buildUrl(long accountNumber, String ifscCode, String url) throws URISyntaxException {
        HttpGet get = new HttpGet(url);
        URI uri = new URIBuilder(get.getURI())
                .addParameter("accountNumber", String.valueOf(accountNumber))
                .addParameter("ifscCode", ifscCode)
                .build();
        get.setURI(uri);
        return get;
    }
}
