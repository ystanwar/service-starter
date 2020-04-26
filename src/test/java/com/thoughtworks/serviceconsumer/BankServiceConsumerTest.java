package com.thoughtworks.serviceconsumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.thoughtworks.BankClient.api.BankDetailsApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "bankservice_provider", port = "8082")
public class BankServiceConsumerTest {

    @Autowired
    BankDetailsApi bankDetailsApi;


    @Pact(provider = "bankservice_provider", consumer = "bankservice_consumer")
    public RequestResponsePact createAPactForValidCheckDetails(PactDslWithProvider builder) {

        return builder
                .given("test checkdetails")
                .uponReceiving("check details of the account")
                .path("/checkDetails")
                .matchQuery("accountNumber", "^[0-9]+$", "12345")
                .matchQuery("ifscCode", "^[A-Z]{4}[0-9]{4}$", "HDFC1234")
                .method("GET")
                .willRespondWith()
                .status(200)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createAPactForValidCheckDetails")
    void test(MockServer mockServer) throws IOException {
        bankDetailsApi.getApiClient().setBasePath(mockServer.getUrl());
        bankDetailsApi.checkDetails(12345L, "HDFC1234");
    }


    @Pact(provider = "bankservice_provider", consumer = "bankservice_consumer")
    public RequestResponsePact createPactForInvalidCheckDetails(PactDslWithProvider builder) {

        return builder
                .given("test checkdetails")
                .uponReceiving("check details of the account")
                .path("/checkDetails")
                .matchQuery("accountNumber", "^[0-9]+$", "123456789")
                .matchQuery("ifscCode", "^[A-Z]{4}[0-9]{4}$", "ABCD1234")
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPactForInvalidCheckDetails")
    void testForNotFound(MockServer mockServer) {
        bankDetailsApi.getApiClient().setBasePath(mockServer.getUrl());
        assertThrows(HttpClientErrorException.class, () -> bankDetailsApi.checkDetails(123456789L, "ZZZZ1234"));
    }


    @Pact(provider = "bankservice_provider", consumer = "bankservice_consumer")
    public RequestResponsePact createPactForCheckingDetailsForInternalServerError(PactDslWithProvider builder) {

        return builder
                .given("test checkdetails")
                .uponReceiving("check details of the account")
                .path("/checkDetails")
                .matchQuery("accountNumber", "^[0-9]+$", "123455555")
                .matchQuery("ifscCode", "^[A-Z]{4}[0-9]{4}$", "ZZZZ1234")
                .method("GET")
                .willRespondWith()
                .status(500)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPactForCheckingDetailsForInternalServerError")
    void testForInternalServerError(MockServer mockServer) {
        bankDetailsApi.getApiClient().setBasePath(mockServer.getUrl());
        assertThrows(HttpServerErrorException.class, () ->bankDetailsApi.checkDetails(12345588789797555L, "ZZZZ1234"));
    }
}
