package com.thoughtworks.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Payment create(@RequestBody Payment payment) throws Exception {
        return paymentService.create(payment);
    }
}
