package com.thoughtworks.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Payment create(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }

}
