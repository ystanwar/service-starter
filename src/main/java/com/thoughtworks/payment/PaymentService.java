package com.thoughtworks.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    public Payment create(Payment note) {
        return paymentRepository.save(note);
    }

}
