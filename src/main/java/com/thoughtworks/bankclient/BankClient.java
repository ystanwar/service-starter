package com.thoughtworks.bankclient;

import com.google.gson.JsonObject;
import com.thoughtworks.payment.BankDetails;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;


@Service
public class BankClient {
    private String baseUrl = "http://localhost:8082";

    public int checkBankDetails(BankDetails bankDetails) throws Exception {
        String url = baseUrl + "/checkDetails";
        String charset = "UTF-8";
        String accountNumber = String.valueOf(bankDetails.getAccountNumber());
        String ifscCode = bankDetails.getIfscCode();
        String query = String.format("accountNumber=%s&&ifscCode=%s",
                URLEncoder.encode(accountNumber, charset),
                URLEncoder.encode(ifscCode, charset));
        URL connection = new URL(url + "?" + query);
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection.openConnection();
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestMethod("GET");
        int responseCode = httpURLConnection.getResponseCode();
        httpURLConnection.disconnect();
        return responseCode;
    }

}
