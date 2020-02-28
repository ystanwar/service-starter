package com.thoughtworks.fraudclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.payment.model.BankDetails;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.serviceclients.FraudClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FraudClientTest {

    @Autowired
    FraudClient fraudClient;

    @Test
    public void checkClientWhenNoFraudReturned() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8083);
        wireMockServer.start();
        configureFor("localhost", 8083);

        stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(200)));

        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        assertTrue(fraudClient.checkFraud(payment));
        wireMockServer.stop();
    }

    @Test
    public void checkClientWhenFraudReturned() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8083);
        wireMockServer.start();
        configureFor("localhost", 8083);

        stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(422)));

        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12345, "HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        assertFalse(fraudClient.checkFraud(payment));
        wireMockServer.stop();
    }

    @Test
    public void checkClientWhenServerErrorReturned() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8083);
        wireMockServer.start();
        configureFor("localhost", 8083);

        stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(500)));

        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12345, "HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        DependencyException dex = assertThrows(DependencyException.class, () -> fraudClient.checkFraud(payment));
        assertEquals("FRAUDSERVICE_FAILURE", dex.getErrorCode());
        assertEquals("received 500", dex.getErrorMessage());
        wireMockServer.stop();
    }

    @Test
    public void checkClientWhenServiceNotReachable() throws Exception {

        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12345, "HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        DependencyException dex = assertThrows(DependencyException.class, () -> fraudClient.checkFraud(payment));
        assertEquals("FRAUDSERVICE_FAILURE", dex.getErrorCode());
        assertEquals("UNAVAILABLE", dex.getErrorMessage());
    }
}
