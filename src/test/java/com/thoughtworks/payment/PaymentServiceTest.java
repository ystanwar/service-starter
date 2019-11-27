package com.thoughtworks.payment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Test
    public void createAPayment() {
        BankDetails beneficiary = new BankDetails(2, "user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails(1, "user2", 12346, "HDFC1234");

        Payment payment = new Payment(1, 100, beneficiary, payee);

        Payment savedPayment = paymentService.create(payment);
        Assertions.assertEquals(1, savedPayment.getId());
        Assertions.assertEquals(100, savedPayment.getAmount());
        Assertions.assertEquals(beneficiary.getAccountNumber(), savedPayment.getBeneficiary_accountNumber());
        Assertions.assertEquals(payee.getAccountNumber(), savedPayment.getPayee_accountNumber());

    }
}
