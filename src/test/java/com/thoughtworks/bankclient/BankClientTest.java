package com.thoughtworks.bankclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.thoughtworks.bank.Bank;
import com.thoughtworks.bank.BankService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BankClientTest {


    @Autowired
    BankClient bankClient;

    @MockBean
    BankService bankService;

    @Test
    public void checkBankDetails() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
        configureFor("localhost", 8082);

        when(bankService.fetchBankByBankCode(anyString())).thenReturn(new Bank("HDFC", "http://localhost:8082"));
        StubMapping string = stubFor(get(urlEqualTo("/checkDetails?accountNumber=12345&ifscCode=HDFC1234")).willReturn(aResponse().withStatus(200)));

        assertEquals(200, bankClient.checkBankDetails(12345, "HDFC1234"));
        wireMockServer.stop();
    }

    @Test
    public void failsToCheckBankDetails() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
        configureFor("localhost", 8082);

        when(bankService.fetchBankByBankCode(anyString())).thenReturn(new Bank("HDFC", "http://localhost:8082"));
        stubFor(get(urlEqualTo("/checkDetails?accountNumber=0000&ifscCode=HDFC1234")).willReturn(aResponse().withStatus(404)));

        assertEquals(404, bankClient.checkBankDetails(0000, "HDFC1234"));
        wireMockServer.stop();
    }

    @Test
    public void checkBankDetailsOfInAnotherBank() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(8084);
        wireMockServer.start();
        configureFor("localhost", 8084);

        when(bankService.fetchBankByBankCode(anyString())).thenReturn(new Bank("AXIS", "http://localhost:8084"));
        StubMapping string = stubFor(get(urlEqualTo("/checkDetails?accountNumber=12345&ifscCode=AXIS1234")).willReturn(aResponse().withStatus(200)));

        assertEquals(200, bankClient.checkBankDetails(12345, "AXIS1234"));
        wireMockServer.stop();
    }
}
