package com.thoughtworks.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.api.api.model.BankDetails;
import com.thoughtworks.api.api.model.PaymentRequest;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.handlers.ExceptionMessageHandler;
import com.thoughtworks.payment.PaymentController;
import com.thoughtworks.serviceclients.BankClient;
import com.thoughtworks.serviceclients.FraudClient;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.Valid;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LoggerTest {
    @MockBean
    BankClient bankClient;
    @MockBean
    FraudClient fraudClient;
    @Autowired
    PaymentController paymentController;

    @Autowired
    ExceptionMessageHandler exceptionHandler;

    @Test
    void checkInfoLogMessageHasAllFields() throws Exception {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(PaymentController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);
        ObjectMapper mapper = new ObjectMapper();
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);
        paymentController.create1(getPaymentRequest());
        for(ILoggingEvent logEvent: listAppender.list){
            JsonNode actualLogMessage = mapper.readTree(logEvent.getFormattedMessage());
            assertTrue(actualLogMessage.has("details"));
            assertTrue(actualLogMessage.has("description"));
            assertTrue(actualLogMessage.has("eventCode"));
            assertEquals(3,actualLogMessage.size());
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

        exceptionHandler.handleResourceNotFoundException(new ResourceNotFoundException("exception","test logging message"));
        for(ILoggingEvent logEvent : listAppender.list){
            JsonNode actualLogMessage = mapper.readTree(logEvent.getFormattedMessage());
            assertTrue(actualLogMessage.has("exception"));
            assertTrue(actualLogMessage.has("stackTrace"));
            assertTrue(actualLogMessage.has("details"));
            assertTrue(actualLogMessage.has("description"));
            assertTrue(actualLogMessage.has("eventCode"));
            assertEquals(5,actualLogMessage.size());
            assertEquals(Level.ERROR, logEvent.getLevel());
        }

    }

    private PaymentRequest getPaymentRequest(){

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
