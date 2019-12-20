package com.thoughtworks.bankclient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class BankClient {

    private String baseUrl;

    @Autowired
    private BankClient(Environment env) {
        this.baseUrl = env.getProperty("bankService");
    }

    public int checkBankDetails(long accountNumber, String ifscCode) throws IOException, URISyntaxException {
        String url = baseUrl + "/checkDetails";
        HttpGet get = new HttpGet(url);
        URI uri = new URIBuilder(get.getURI())
                .addParameter("accountNumber", String.valueOf(accountNumber))
                .addParameter("ifscCode", ifscCode)
                .build();
        get.setURI(uri);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        response.close();
        return statusCode;
    }
}
