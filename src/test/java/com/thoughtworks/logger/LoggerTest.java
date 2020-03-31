package com.thoughtworks.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.api.api.model.BankDetails;
import com.thoughtworks.api.api.model.PaymentRequest;
import com.thoughtworks.api.api.model.PaymentSuccessResponse;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.filter.CustomRequestLoggingFilter;
import com.thoughtworks.handlers.ExceptionMessageHandler;
import com.thoughtworks.payment.PaymentController;
import com.thoughtworks.payment.PaymentRepository;
import com.thoughtworks.payment.PaymentService;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import com.thoughtworks.serviceclients.BankClient;
import com.thoughtworks.serviceclients.FraudClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.Valid;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import({MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class, CollectorRegistry.class})
public class LoggerTest {
    @MockBean
    BankClient bankClient;
    @MockBean
    FraudClient fraudClient;
    @Autowired
    PaymentController paymentController;

    @Autowired
    ExceptionMessageHandler exceptionHandler;

    @Autowired
    MeterRegistry meterRegistry;

    @Autowired
    CollectorRegistry collectorRegistry;

    @MockBean
    PaymentService paymentService;

    @MockBean
    PaymentRepository paymentRepository;

    @MockBean
    Prometheus prometheus;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        when(prometheus.getPaymentsCounter()).thenReturn(Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments")
                .register(meterRegistry));
    }

    @AfterEach
    void tearDown() {
        collectorRegistry.clear();
    }


    @Test
    void checkInfoLogMessageHasAllFields() throws Exception {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(PaymentController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);
        ObjectMapper mapper = new ObjectMapper();
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12L).ifscCode("HDFC1");
        BankDetails payee = new BankDetails().name("user2").accountNumber(12346L).ifscCode("HDFC1234");

        Payment payment = new Payment(500, beneficiary, payee);
        payment.setId(1);
        when(paymentService.create(any(PaymentRequest.class))).thenReturn(payment);
        paymentController.create1(getPaymentRequest());
        for (ILoggingEvent logEvent : listAppender.list) {
            JsonNode actualLogMessage = mapper.readTree(logEvent.getFormattedMessage());
            assertTrue(actualLogMessage.has("details"));
            assertTrue(actualLogMessage.has("description"));
            assertTrue(actualLogMessage.has("eventCode"));
            assertEquals(3, actualLogMessage.size());
            assertEquals(Level.INFO, logEvent.getLevel());
        }
    }

    @Test
    public void checkIfErrorLogMessageHasAllFields() throws IOException {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(ExceptionMessageHandler.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);
        ObjectMapper mapper = new ObjectMapper();

        exceptionHandler.handleResourceNotFoundException(new ResourceNotFoundException("exception", "test logging message"));
        for (ILoggingEvent logEvent : listAppender.list) {
            JsonNode actualLogMessage = mapper.readTree(logEvent.getFormattedMessage());
            assertTrue(actualLogMessage.has("exception"));
            assertTrue(actualLogMessage.has("stackTrace"));
            assertTrue(actualLogMessage.has("details"));
            assertTrue(actualLogMessage.has("description"));
            assertTrue(actualLogMessage.has("eventCode"));
            assertEquals(5, actualLogMessage.size());
            assertEquals(Level.ERROR, logEvent.getLevel());
        }

    }

    @Test
    void checkFilterRequest() throws Exception {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(CustomRequestLoggingFilter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);
        ObjectMapper objectMapper = new ObjectMapper();
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12L).ifscCode("HDFC1");
        BankDetails payee = new BankDetails().name("user2").accountNumber(12346L).ifscCode("HDFC1234");

        Payment payment = new Payment(500, beneficiary, payee);
        payment.setId(1);
        PaymentSuccessResponse response = new PaymentSuccessResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(payment.getId());
        when(paymentService.create(any(PaymentRequest.class))).thenReturn(payment);

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));

        ObjectMapper mapper = new ObjectMapper();


        for (ILoggingEvent logEvent : listAppender.list) {
            JsonNode actualLogMessage = mapper.readTree(logEvent.getFormattedMessage());
            assertTrue(actualLogMessage.has("details"));
            assertTrue(actualLogMessage.has("description"));
            assertTrue(actualLogMessage.has("eventCode"));
            assertTrue(actualLogMessage.has("exception"));
            assertEquals(4, actualLogMessage.size());
            assertEquals(Level.INFO, logEvent.getLevel());
        }

    }


    private PaymentRequest getPaymentRequest() {

        BankDetails beneficiary = new BankDetails();
        beneficiary.setName("user1");
        beneficiary.setAccountNumber((long) 12345);
        beneficiary.setIfscCode("HDFC1234");

        BankDetails payee = new com.thoughtworks.api.api.model.BankDetails();
        payee.setName("user2");
        payee.setAccountNumber((long) 67890);
        payee.setIfscCode("HDFC1234");

        com.thoughtworks.api.api.model.@Valid PaymentRequest paymentRequest = new com.thoughtworks.api.api.model.PaymentRequest();
        paymentRequest.setAmount(100);
        paymentRequest.setBeneficiary(beneficiary);
        paymentRequest.setPayee(payee);
        return paymentRequest;
    }

}

