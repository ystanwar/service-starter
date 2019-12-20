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

//    public int checkBankDetails(long accountNumber, String ifscCode) throws Exception {
//        String url = baseUrl + "/checkDetails";
//        String charset = "UTF-8";
//        String query = String.format("accountNumber=%s&&ifscCode=%s",
//                URLEncoder.encode(String.valueOf(accountNumber), charset),
//                URLEncoder.encode(ifscCode, charset));
//        URL connection = new URL(url + "?" + query);
//        return getResponseCode(connection);
//    }
//
//    private int getResponseCode(URL connection) throws IOException {
//        HttpURLConnection httpURLConnection = (HttpURLConnection) connection.openConnection();
//        httpURLConnection.setRequestProperty("Content-Type", "application/json");
//        httpURLConnection.setRequestProperty("Accept", "application/json");
//        httpURLConnection.setRequestMethod("GET");
//        int responseCode = httpURLConnection.getResponseCode();
//        httpURLConnection.disconnect();
//        return responseCode;
//    }
    public int checkBankDetails(long accountNumber, String ifscCode) throws IOException, URISyntaxException {
        System.out.println(accountNumber+ifscCode);
       // String url = baseUrl + "/checkDetails";
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http")
                .setHost("localhost")
                .setPort(8082)
                .setPath("/checkDetails")
//                .setParameter("accountNumber",String.valueOf(accountNumber))
//                .setParameter("ifscCode",ifscCode);
                .addParameter("accountNumber",String.valueOf(accountNumber))
                .addParameter("ifscCode",ifscCode);

        URI uri = uriBuilder.build();
        System.out.println(uri);
        HttpGet get = new HttpGet(uri);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response=httpclient.execute(get);
        return response.getStatusLine().getStatusCode();

    }
}
