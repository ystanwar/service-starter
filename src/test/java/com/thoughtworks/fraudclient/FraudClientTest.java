package com.thoughtworks.fraudclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.thoughtworks.payment.BankDetails;
import com.thoughtworks.payment.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FraudClientTest {

    @Autowired
    FraudClient fraudClient;

    @Test
    public void checkFraud() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8083);
        wireMockServer.start();
        configureFor("localhost", 8083);

        stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(200)));

        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        assertEquals(200, fraudClient.checkFraud(payment));
        wireMockServer.stop();
    }
}
