package com.thoughtworks.payment;

import com.thoughtworks.bankclient.BankClient;
import com.thoughtworks.bankclient.BankInfoNotFoundException;
import com.thoughtworks.payment.model.BankDetails;
import com.thoughtworks.payment.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @MockBean
    BankClient bankClient;

    @Test
    public void createAPayment() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true);

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

        BeneficiaryAccountDetailsNotFound exception = assertThrows(BeneficiaryAccountDetailsNotFound.class, () -> paymentService.create(payment));

        assertEquals("user1's AccountDetails Not Found", exception.getValue());
    }

    @Test
    public void paymentTransactionFailsIfPayeeAccountNotFound() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 00000, "0000000");

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(true, false);

        Payment payment = new Payment(100, beneficiary, payee);

        PayeeAccountDetailsNotFound exception = assertThrows(PayeeAccountDetailsNotFound.class, () -> paymentService.create(payment));

        assertEquals("user2's AccountDetails Not Found", exception.getValue());
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
        when(bankClient.checkBankDetails(anyLong(), eq("AAAAAAA"))).thenThrow(BankInfoNotFoundException.class);
        when(bankClient.checkBankDetails(anyLong(), eq("HDFC1234"))).thenReturn(true);

        assertThrows(BankInfoNotFoundException.class, () -> paymentService.create(new Payment(100, bankDetailsWithInvalidIfscCode, validBankDetails)));
        assertThrows(BankInfoNotFoundException.class, () -> paymentService.create(new Payment(100, validBankDetails, bankDetailsWithInvalidIfscCode)));
    }
}
