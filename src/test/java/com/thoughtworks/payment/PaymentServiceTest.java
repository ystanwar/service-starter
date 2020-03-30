package com.thoughtworks.payment;

import com.thoughtworks.api.api.model.*;
import com.thoughtworks.exceptions.PaymentRefusedException;
import com.thoughtworks.exceptions.ResourceNotFoundException;

import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.serviceclients.BankClient;
import com.thoughtworks.serviceclients.FraudClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @MockBean
    BankClient bankClient;

    @MockBean
    FraudClient fraudClient;

    @MockBean
    RestTemplate restTemplate;

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    public void createAPayment() throws Exception {
        
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(67890L).ifscCode("HDFC1234");

        PaymentRequest paymentRequest = new PaymentRequest().amount(100).beneficiary(beneficiary).payee(payee);
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);

        Payment savedPayment = paymentService.create(paymentRequest);
        assertEquals(100, savedPayment.getAmount());
        assertEquals(beneficiary.getName(), savedPayment.getBeneficiaryName());
        assertEquals(beneficiary.getAccountNumber(), savedPayment.getBeneficiaryAccountNumber());
        assertEquals(beneficiary.getIfscCode(), savedPayment.getBeneficiaryIfscCode());

        assertEquals(payee.getName(), savedPayment.getPayeeName());
        assertEquals(payee.getAccountNumber(), savedPayment.getPayeeAccountNumber());
        assertEquals(payee.getIfscCode(), savedPayment.getPayeeIfscCode());

    }

    @Test
    public void paymentTransactionFailsIfBeneficiaryAccountNotFound() throws Exception {
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(00000L).ifscCode("OOOOOO");
        BankDetails payee = new BankDetails().name("user2").accountNumber(67890L).ifscCode("HDFC1234");

        PaymentRequest paymentRequest = new PaymentRequest().amount(100).beneficiary(beneficiary).payee(payee);

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.create(paymentRequest));

        assertEquals("user1's AccountDetails Not Found At OOOOOO", exception.getErrorMessage());
    }

    @Test
    public void paymentTransactionFailsIfPayeeAccountNotFound() throws Exception {
 
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(00000L).ifscCode("0000000");
        PaymentRequest paymentRequest = new PaymentRequest().amount(100).beneficiary(beneficiary).payee(payee);
        
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true, false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.create(paymentRequest));

        assertEquals("user2's AccountDetails Not Found At 0000000", exception.getErrorMessage());
    }

    @Test
    public void testCreatePaymentForInvalidArguments() throws Exception {
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(67890L).ifscCode("HDFC1234");
        
        assertThrows(IllegalArgumentException.class, () -> paymentService.create(null));

        assertThrows(IllegalArgumentException.class, () -> paymentService.create(new PaymentRequest().amount(0).beneficiary(beneficiary).payee(payee)));
        assertThrows(IllegalArgumentException.class, () -> paymentService.create(new PaymentRequest().amount(100).beneficiary(null).payee(payee)));
        // assertThrows(IllegalArgumentException.class, () -> paymentService.create(new PaymentRequest().amount(100).beneficiary(beneficiary).payee(null)));

    }

    @Test
    public void testCreatePaymentForMissingBankDetails() throws Exception {

        BankDetails validBankDetails = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails bankDetailsWithInvalidIfscCode = new BankDetails().name("user2").accountNumber(67890L).ifscCode("AAAAAAA");
       
        when(bankClient.checkBankDetails(anyLong(), eq("AAAAAAA"))).thenThrow(ResourceNotFoundException.class);
        when(bankClient.checkBankDetails(anyLong(), eq("HDFC1234"))).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> paymentService.create(new PaymentRequest().amount(100).beneficiary(bankDetailsWithInvalidIfscCode).payee(validBankDetails)));
        assertThrows(ResourceNotFoundException.class, () -> paymentService.create(new PaymentRequest().amount(100).beneficiary(validBankDetails).payee(bankDetailsWithInvalidIfscCode)));

    }

    @Test
    public void testCreatePaymentWithFraudulentInput() throws Exception {
        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(12345L).ifscCode("HDFC1234");
      
        PaymentRequest paymentRequest = new PaymentRequest().amount(100).beneficiary(beneficiary).payee(payee);
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenThrow(PaymentRefusedException.class);
        assertThrows(PaymentRefusedException.class, () -> paymentService.create(paymentRequest));
    }

    @Test
    public void testFindAll() throws Exception {

        BankDetails beneficiary = new BankDetails().name("user1").accountNumber(12345L).ifscCode("HDFC1234");
        BankDetails payee = new BankDetails().name("user2").accountNumber(67890L).ifscCode("HDFC1234");
        

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);
        for (int i = 0; i < 10; i++) {
            PaymentRequest paymentRequest = new PaymentRequest().amount(100 + i * 10).beneficiary(beneficiary).payee(payee);
            paymentService.create(paymentRequest);
        }

        List<Payment> allPayments = paymentService.getAll();
        assertEquals(10, allPayments.size());

        for (int i = 0; i < allPayments.size(); i++) {
            assertEquals(i + 1, allPayments.get(i).getId());
        }
    }
}
