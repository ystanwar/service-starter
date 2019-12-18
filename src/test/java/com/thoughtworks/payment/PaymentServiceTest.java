package com.thoughtworks.payment;

import com.thoughtworks.bankclient.BankClient;
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
        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(200);

        Payment savedPayment = paymentService.create(payment);
        assertEquals(100, savedPayment.getAmount());
        assertEquals(beneficiary, savedPayment.getBeneficiary());
        assertEquals(payee, savedPayment.getPayee());
    }

    @Test
    public void paymentTransactionFailsIfBeneficiaryAccountNotFound() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 00000, "OOOOOO");
        BankDetails payee = new BankDetails("user2", 67890, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(404);

        BeneficiaryAccountDetailsNotFound exception = assertThrows(BeneficiaryAccountDetailsNotFound.class, () -> paymentService.create(payment));

        assertEquals("user1's AccountDetails Not Found", exception.getValue());
    }

    @Test
    public void paymentTransactionFailsIfPayeeAccountNotFound() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 00000, "0000000");

        when(bankClient.checkBankDetails(anyLong(), anyString())).thenReturn(200, 404);

        Payment payment = new Payment(100, beneficiary, payee);

        PayeeAccountDetailsNotFound exception = assertThrows(PayeeAccountDetailsNotFound.class,() -> paymentService.create(payment));

        assertEquals("user2's AccountDetails Not Found", exception.getValue());
    }
}
