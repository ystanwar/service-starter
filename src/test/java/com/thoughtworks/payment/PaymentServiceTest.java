package com.thoughtworks.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Test
    public void createAPayment() {
        BankDetails beneficiary = new BankDetails("user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(100, beneficiary, payee);

        Payment savedPayment = paymentService.create(payment);
        assertEquals(1, savedPayment.getId());
        assertEquals(100, savedPayment.getAmount());
        assertEquals(beneficiary, savedPayment.getBeneficiary());
        assertEquals(payee, savedPayment.getPayee());
    }
}
