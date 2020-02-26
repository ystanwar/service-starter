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
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    RetryRegistry retryRegistry;

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
        circuitBreakerRegistry.circuitBreaker("bankservice").reset();
    }

    @AfterEach
    void clearMDC() {
        MDC.clear();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void circuitBreakerOpensAfterFiftyPercentThresholdFailureLimitAndDoesNotAllowRequests() throws Exception {
        when(bankInfoService.fetchBankByBankCode(any(String.class))).thenReturn(new BankInfo("HDFC", "http://localhost:8088"));

        assertThrows(DependencyException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        assertThrows(CallNotPermittedException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(4)).fetchBankByBankCode(any(String.class));
    }

    @Test
    public void circuitBreakerChangesItsStateFromOpenToClosed() throws Exception {
        CircuitBreaker circuitBreaker=circuitBreakerRegistry.circuitBreaker("bankservice");
        stubFor(get(urlEqualTo("/checkDetails?accountNumber=12345&ifscCode=HDFC1234")).willReturn(aResponse().withStatus(200)));

        when(bankInfoService.fetchBankByBankCode(anyString()))
                .thenReturn(
                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),

                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),

                        new BankInfo("HDFC", "http://localhost:8082"),
                        new BankInfo("HDFC", "http://localhost:8082"),
                        new BankInfo("HDFC", "http://localhost:8088"),
                        new BankInfo("HDFC", "http://localhost:8088"),

                        new BankInfo("HDFC", "http://localhost:8082"),
                        new BankInfo("HDFC", "http://localhost:8082"),
                        new BankInfo("HDFC", "http://localhost:8082"),
                        new BankInfo("HDFC", "http://localhost:8082"),
                        new BankInfo("HDFC", "http://localhost:8082")

                );

        assertThrows(DependencyException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(3)).fetchBankByBankCode(any(String.class));
        assertEquals("CLOSED", circuitBreaker.getState().name());;
        assertThrows(CallNotPermittedException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(4)).fetchBankByBankCode(any(String.class));
        assertEquals("OPEN", circuitBreaker.getState().name());;

        TimeUnit.SECONDS.sleep(5);
        assertThrows(DependencyException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        assertEquals("HALF_OPEN", circuitBreaker.getState().name());;
        assertThrows(CallNotPermittedException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(8)).fetchBankByBankCode(any(String.class));
        assertEquals("OPEN", circuitBreaker.getState().name());;

        TimeUnit.SECONDS.sleep(5);
        assertEquals(true, bankClient.checkBankDetails(12345, "HDFC1234"));
        assertEquals("HALF_OPEN", circuitBreaker.getState().name());;
        assertEquals(true, bankClient.checkBankDetails(12345, "HDFC1234"));
        assertThrows(CallNotPermittedException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(12)).fetchBankByBankCode(any(String.class));
        assertEquals("OPEN", circuitBreaker.getState().name());;

        TimeUnit.SECONDS.sleep(5);
        assertTrue(bankClient.checkBankDetails(12345, "HDFC1234"));
        assertEquals("HALF_OPEN", circuitBreaker.getState().name());;
        assertTrue(bankClient.checkBankDetails(12345, "HDFC1234"));
        assertTrue(bankClient.checkBankDetails(12345, "HDFC1234"));
        assertTrue(bankClient.checkBankDetails(12345, "HDFC1234"));
        assertEquals("CLOSED", circuitBreaker.getState().name());;
        assertTrue(bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(17)).fetchBankByBankCode(any(String.class));
        assertEquals("CLOSED", circuitBreaker.getState().name());;

    }

    @Test
    public void requestIsRetriedAfterReceivingDependencyException() throws Exception {
        when(bankInfoService.fetchBankByBankCode(any(String.class))).thenReturn(new BankInfo("HDFC", "http://localhost:8088"));
        assertThrows(DependencyException.class, () -> bankClient.checkBankDetails(12345, "HDFC1234"));
        verify(bankInfoService, times(3)).fetchBankByBankCode(any(String.class));
    }

    @Test
    public void testCheckBankDetailsSuccess() throws Exception {

        String requestID = String.valueOf(UUID.randomUUID());
        MDC.put("request.id", requestID);
        when(bankInfoService.fetchBankByBankCode(anyString())).thenReturn(new BankInfo("HDFC", "http://localhost:8082"));
        stubFor(
                get(urlEqualTo("/checkDetails?accountNumber=12345&ifscCode=HDFC1234"))
                        .withHeader("PARENT_REQ_ID", matching("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))
                        .willReturn(aResponse().withStatus(200)));

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
