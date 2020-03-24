package com.thoughtworks.fraudclient;

import com.thoughtworks.FraudClient.api.CheckFraudApi;
import com.thoughtworks.FraudClient.invoker.ApiClient;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.serviceclients.FraudClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.thoughtworks.api.api.model.BankDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FraudClientTest {

    @Autowired
    FraudClient fraudClient;

    @MockBean
    RestTemplate restTemplate;

    @MockBean
    CheckFraudApi checkFraudApi;

    @Test
    public void checkClientWhenNoFraudReturned() throws Exception {
//        WireMockServer wireMockServer = new WireMockServer(8083);
//        wireMockServer.start();
//        configureFor("localhost", 8083);
//        stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(200)));
        when(checkFraudApi.getApiClient()).thenReturn(new ApiClient());
        doNothing().when(checkFraudApi).checkFraud(any());


        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(67890L).ifscCode("HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        assertTrue(fraudClient.checkFraud(payment));
        //wireMockServer.stop();
    }

    @Test
    public void checkClientWhenFraudReturned() throws Exception {
//        WireMockServer wireMockServer = new WireMockServer(8083);
//        wireMockServer.start();
//        configureFor("localhost", 8083);
        //stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(422)));
        when(checkFraudApi.getApiClient()).thenReturn(new ApiClient());
        doThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY)).when(checkFraudApi).checkFraud(any());

        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(12345L).ifscCode("HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        assertFalse(fraudClient.checkFraud(payment));
        //wireMockServer.stop();
    }

    @Test
    public void checkClientWhenServerErrorReturned() throws Exception {
//        WireMockServer wireMockServer = new WireMockServer(8083);
//        wireMockServer.start();
//        configureFor("localhost", 8083);
//        stubFor(post(urlEqualTo("/checkFraud")).willReturn(aResponse().withStatus(500)));

        when(checkFraudApi.getApiClient()).thenReturn(new ApiClient());
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)).when(checkFraudApi).checkFraud(any());
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(12345L).ifscCode("HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        DependencyException dex = assertThrows(DependencyException.class, () -> fraudClient.checkFraud(payment));
        assertEquals("FRAUDSERVICE_FAILURE", dex.getErrorCode());
        assertEquals("received 500", dex.getErrorMessage());
        // wireMockServer.stop();
    }

    @Test
    public void checkClientWhenServiceNotReachable() throws Exception {
        when(checkFraudApi.getApiClient()).thenReturn(new ApiClient());
        doThrow(RestClientException.class).when(checkFraudApi).checkFraud(any());

        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(12345L).ifscCode("HDFC1234");
        Payment payment = new Payment(100, beneficiary, payee);

        DependencyException dex = assertThrows(DependencyException.class, () -> fraudClient.checkFraud(payment));
        assertEquals("FRAUDSERVICE_FAILURE", dex.getErrorCode());
        assertEquals("UNAVAILABLE", dex.getErrorMessage());
    }
}
