package com.thoughtworks.serviceconsumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.thoughtworks.BankClient.api.BankDetailsApi;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "bankservice_provider", port = "8082")
public class BankServiceConsumerTest {

    @Autowired
    BankDetailsApi bankDetailsApi;



    @Pact(provider = "bankservice_provider", consumer = "bankservice_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
//        PactDslJsonBody bodyResponse = new PactDslJsonBody()
//                .stringValue("productName", "TV")
//                .stringType("locationName", "CHENNAI")
//                .integerType("quantity", 100);

        System.out.println("hi iam in provider");

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
    void test(MockServer mockServer) throws IOException {
        bankDetailsApi.getApiClient().setBasePath(mockServer.getUrl());
        bankDetailsApi.checkDetails(12345L, "HDFC1234");

    }

}
