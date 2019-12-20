package com.thoughtworks.fraudclient;

import com.thoughtworks.payment.Payment;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FraudClient {
    private String baseUrl;

    @Autowired
    FraudClient(Environment env) {
        this.baseUrl = env.getProperty("fraudService");
    }

    public int checkFraud(Payment payment) throws Exception {
        String url = baseUrl + "/checkFraud";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("payment", String.valueOf(payment)));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        int responseCode = response.getStatusLine().getStatusCode();
        client.close();
        return responseCode;
    }
}
