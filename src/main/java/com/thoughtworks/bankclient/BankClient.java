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

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountHolderName", bankDetails.getName());
        jsonObject.addProperty("accountNumber", bankDetails.getAccountNumber());
        jsonObject.addProperty("ifscCode", bankDetails.getIfscCode());

        String bankDetail = jsonObject.toString();
        String query = String.format("account=%s",
                URLEncoder.encode(bankDetail, charset));
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
