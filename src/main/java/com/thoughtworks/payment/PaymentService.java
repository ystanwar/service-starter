package com.thoughtworks.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankDetailsRepository bankDetailsRepository;

    public Payment create(Payment payment) {
        bankDetailsRepository.save(payment.getBeneficiary());
        bankDetailsRepository.save(payment.getPayee());
        return paymentRepository.save(payment);
    }

}
