package com.thoughtworks.fraudclient;

import com.google.gson.Gson;
import com.thoughtworks.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class FraudClient {
    private String baseUrl;

    @Autowired
    FraudClient(Environment env) {
        this.baseUrl = env.getProperty("fraudService");
    }

    public int checkFraud(Payment payment) throws Exception {
        String url = baseUrl + "/checkFraud";
        Gson gson = new Gson();
        String paymentData = gson.toJson(payment);
        URL connection = new URL(url);
        return getResponseCode(connection, paymentData);
    }

    private int getResponseCode(URL connection, String paymentData) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection.openConnection();
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
        out.write(paymentData);
        out.close();
        return httpURLConnection.getResponseCode();
    }
}
