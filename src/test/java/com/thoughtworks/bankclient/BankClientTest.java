package com.thoughtworks.bankclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.bankInfo.BankInfoService;
import com.thoughtworks.exceptions.DependencyException;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.exceptions.ValidationException;
import com.thoughtworks.serviceclients.BankClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;


@SpringBootTest
public class BankClientTest {

    @Autowired
    BankClient bankClient;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    BankInfoService bankInfoService;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
        configureFor("localhost", 8082);

    }

    @BeforeEach
    void circuitBreakerSetup() {
        circuitBreakerRegistry.circuitBreaker("service1").reset();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void circuitBreakerOpensAfterFiftyPercentThresholdFailureLimitAndDoesNotAllowRequests() {
        when(bankInfoService.fetchBankByBankCode(any(String.class))).thenReturn(new BankInfo("HDFC", "http://localhost:8088"));
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("service1");

        for (int i = 0; i < 5; i++) {
            try {
                Supplier<Boolean> decoratedMethod = CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                    try {
                        return bankClient.checkBankDetails(12345, "HDFC1234");
                    } catch (Exception bankClientException) {
                        assertEquals(DependencyException.class, bankClientException.getClass());
                        return false;
                    }
                });
                decoratedMethod.get();
            } catch (Exception circuitBreakerException) {
                assertEquals(CallNotPermittedException.class, circuitBreakerException.getClass());
            }
        }
        verify(bankInfoService, times(3)).fetchBankByBankCode(any(String.class));
    }

    @Test
    public void testCheckBankDetailsSuccess() throws Exception {

        when(bankInfoService.fetchBankByBankCode(anyString())).thenReturn(new BankInfo("HDFC", "http://localhost:8082"));
        stubFor(get(urlEqualTo("/checkDetails?accountNumber=12345&ifscCode=HDFC1234")).willReturn(aResponse().withStatus(200)));

        assertEquals(true, bankClient.checkBankDetails(12345, "HDFC1234"));
    }

    @Test
    public void testCheckBankDetailsWithWrongIfscCode() throws Exception {

        when(bankInfoService.fetchBankByBankCode("XXYY")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> bankClient.checkBankDetails(12345, "XXYY1234"));

    }

    @Test
    public void testCheckBankDetailsWithInvalidFormatIfscCode() throws Exception {
        assertThrows(ValidationException.class, () -> bankClient.checkBankDetails(12345, ""));
        assertThrows(ValidationException.class, () -> bankClient.checkBankDetails(12345, "H"));
        assertThrows(ValidationException.class, () -> bankClient.checkBankDetails(12345, "HDFC"));
        assertThrows(ValidationException.class, () -> bankClient.checkBankDetails(12345, null));
    }

    @Test
    public void testCheckBankDetailsForInvalidAccount() throws Exception {
        when(bankInfoService.fetchBankByBankCode(anyString())).thenReturn(new BankInfo("HDFC", "http://localhost:8082"));
        stubFor(get(urlEqualTo("/checkDetails?accountNumber=0&ifscCode=HDFC1234")).willReturn(aResponse().withStatus(404)));

        assertEquals(false, bankClient.checkBankDetails(0, "HDFC1234"));
    }

    @Test
    public void testCheckBankDetailsForWrongBaseUrl() throws Exception {

        when(bankInfoService.fetchBankByBankCode(anyString())).thenReturn(new BankInfo("HDFC", "http://localhost:808"));

        assertThrows(DependencyException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
    }

    @Test
    public void testCheckBankDetailsForBankServiceErrors() throws DependencyException {
        when(bankInfoService.fetchBankByBankCode(anyString())).thenReturn(new BankInfo("HDFC", "http://localhost:8082"));
        stubFor(get(urlEqualTo("/checkDetails?accountNumber=12345&ifscCode=HDFC1234")).willReturn(aResponse().withStatus(500)));

        DependencyException exception = assertThrows(DependencyException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        assertEquals("BankService - HDFC1234", exception.getKey());
        assertEquals("SERVICE_ERROR - 500", exception.getValue());
    }
}
