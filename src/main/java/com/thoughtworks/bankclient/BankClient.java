package com.thoughtworks.bankclient;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class BankClient {
    private String baseUrl = "http://localhost:8082";

    public int checkBankDetails(long accountNumber, String ifscCode) throws Exception {
        String url = baseUrl + "/checkDetails";
        String charset = "UTF-8";
        String query = String.format("accountNumber=%s&&ifscCode=%s",
                URLEncoder.encode(String.valueOf(accountNumber), charset),
                URLEncoder.encode(ifscCode, charset));
        URL connection = new URL(url + "?" + query);
        return getResponseCode(connection);
    }

    private int getResponseCode(URL connection) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection.openConnection();
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestMethod("GET");
        int responseCode = httpURLConnection.getResponseCode();
        httpURLConnection.disconnect();
        return responseCode;
    }
}
