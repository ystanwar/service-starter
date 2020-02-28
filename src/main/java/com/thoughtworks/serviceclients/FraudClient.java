package com.thoughtworks.serviceclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.payment.model.Payment;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FraudClient {
    private String baseUrl;

    @Autowired
    FraudClient(Environment env) {
        this.baseUrl = env.getProperty("fraudService");
    }

    public boolean checkFraud(Payment payment) throws Exception {
        String url = baseUrl + "/checkFraud";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        ObjectMapper obj = new ObjectMapper();
        StringEntity entity = new StringEntity(obj.writeValueAsString(payment));
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "application/json");
        int responseCode = 0;
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            client.close();
        } catch (Exception ex) {
            throw new DependencyException("InternalService", "FraudService", url, "UNAVAILABLE", ex);
        }

        if (responseCode == 200) {
            return true;
        } else if (responseCode == 422) {
            return false;
        } else {
            throw new DependencyException("InternalService", "FraudService", url, "received " + responseCode);
        }
    }
}
