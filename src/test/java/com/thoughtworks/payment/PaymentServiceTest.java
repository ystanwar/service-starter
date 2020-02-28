package com.thoughtworks.payment;

import com.thoughtworks.exceptions.PaymentRefusedException;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.payment.model.BankDetails;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.serviceclients.BankClient;
import com.thoughtworks.serviceclients.FraudClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @MockBean
    BankClient bankClient;

    @MockBean
    FraudClient fraudClient;

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    public void createAPayment() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);

        Payment savedPayment = paymentService.create(payment);
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
        BankDetails beneficiary = new BankDetails("user1", 00000, "OOOOOO");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.create(payment));

        assertEquals("user1's AccountDetails Not Found At OOOOOO", exception.getErrorMessage());
    }

    @Test
    public void paymentTransactionFailsIfPayeeAccountNotFound() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 00000, "0000000");

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true, false);

        Payment payment = new Payment(100, beneficiary, payee);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.create(payment));

        assertEquals("user2's AccountDetails Not Found At 0000000", exception.getErrorMessage());
    }

    @Test
    public void testCreatePaymentForInvalidArguments() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");

        assertThrows(IllegalArgumentException.class, () -> paymentService.create(null));
        assertThrows(IllegalArgumentException.class, () -> paymentService.create(new Payment(0, beneficiary, payee)));
        assertThrows(IllegalArgumentException.class, () -> paymentService.create(new Payment(1000, null, payee)));
        assertThrows(IllegalArgumentException.class, () -> paymentService.create(new Payment(1000, beneficiary, null)));

    }

    @Test
    public void testCreatePaymentForMissingBankDetails() throws Exception {

        BankDetails validBankDetails = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails bankDetailsWithInvalidIfscCode = new BankDetails("user2", 67890, "AAAAAAA");
        when(bankClient.checkBankDetails(anyLong(), eq("AAAAAAA"))).thenThrow(ResourceNotFoundException.class);
        when(bankClient.checkBankDetails(anyLong(), eq("HDFC1234"))).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> paymentService.create(new Payment(100, bankDetailsWithInvalidIfscCode, validBankDetails)));
        assertThrows(ResourceNotFoundException.class, () -> paymentService.create(new Payment(100, validBankDetails, bankDetailsWithInvalidIfscCode)));
    }

    @Test
    public void testCreatePaymentWithFraudulentInput() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12345, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenThrow(PaymentRefusedException.class);
        assertThrows(PaymentRefusedException.class, () -> paymentService.create(payment));
    }

    @Test
    public void testFindAll() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);
        when(fraudClient.checkFraud(any())).thenReturn(true);
        for (int i = 0; i < 10; i++) {
            Payment payment = new Payment(100 + i * 10, beneficiary, payee);
            Payment savedPayment = paymentService.create(payment);
        }

        List<Payment> allPayments = paymentService.getAll();
        System.out.println("Count" + allPayments.size());
        assertEquals(10, allPayments.size());

        for (int i = 0; i < allPayments.size(); i++) {
            assertEquals(i + 1, allPayments.get(i).getId());
        }
    }
}
