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
        System.out.println(accountNumber+ifscCode);
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http")
                .setHost("localhost")
                .setPort(8082)
                .setPath("/checkDetails")
                .addParameter("accountNumber",String.valueOf(accountNumber))
                .addParameter("ifscCode",ifscCode);

        URI uri = uriBuilder.build();
        HttpGet get = new HttpGet(uri);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response=httpclient.execute(get);
        return response.getStatusLine().getStatusCode();

    }
}
