package com.thoughtworks.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.thoughtworks.payment.PaymentController;
import com.thoughtworks.serviceclients.BankClient;
import com.thoughtworks.serviceclients.FraudClient;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.Valid;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void checkLoggerMessage() throws Exception {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(PaymentController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);

        com.thoughtworks.api.api.model.BankDetails beneficiary = new com.thoughtworks.api.api.model.BankDetails();
        beneficiary.setName("user1");
        beneficiary.setAccountNumber((long) 12345);
        beneficiary.setIfscCode("HDFC1234");

        com.thoughtworks.api.api.model.BankDetails payee = new com.thoughtworks.api.api.model.BankDetails();
        payee.setName("user2");
        payee.setAccountNumber((long) 67890);
        payee.setIfscCode("HDFC1234");

        com.thoughtworks.api.api.model.@Valid PaymentRequest paymentRequest = new com.thoughtworks.api.api.model.PaymentRequest();
        paymentRequest.setAmount(100);
        paymentRequest.setBeneficiary(beneficiary);
        paymentRequest.setPayee(payee);
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);
        paymentController.create1(paymentRequest);

        List<ILoggingEvent> logsList = listAppender.list;

        String logMessage = logsList.get(0).getFormattedMessage();
        Level logLevel = logsList.get(0).getLevel();

        assertEquals("{eventCode:PAYMENT_SUCCESSFUL,description:payment successful,details:{\"PaymentId\":\"1\",\"BeneficiaryIfscCode\":\"HDFC1234\",\"PayeeIfscCode\":\"HDFC1234\"}}", logMessage);
        assertEquals(Level.INFO, logLevel);

    }
}
