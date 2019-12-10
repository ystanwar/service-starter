package com.thoughtworks.fraudclient;

import com.google.gson.Gson;
import com.thoughtworks.payment.Payment;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class FraudClient {
    private String baseUrl = "http://localhost:8083";

    public int checkFraud(Payment payment) throws Exception {
        String url = baseUrl + "/checkFraud";
        Gson gson = new Gson();
        String json = gson.toJson(payment);
        URL connection = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection.openConnection();
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
        out.write(json);
        out.close();
        return httpURLConnection.getResponseCode();
    }
}
