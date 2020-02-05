package com.thoughtworks.payment;

import com.thoughtworks.logger.Event;
import com.thoughtworks.payment.message.PaymentResponse;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.Gauge;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@CircuitBreaker(name = "service1")
public class PaymentController {
    @Autowired
    MeterRegistry meterRegistry;

    private Counter paymentsCounter;
    private Counter paymentSuccessCounter;
    private Gauge paymentRequestTime;

    @Autowired
    Prometheus prometheus;

    private static Logger logger = LogManager.getLogger(PaymentController.class);

    @Autowired
    PaymentService paymentService;

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Payment created successfully"),
            @ApiResponse(code = 404, message = "BankAccount not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest paymentRequest) throws Exception {
        paymentRequestTime = prometheus.getPaymentRequestTime();
        long startTime = System.currentTimeMillis();
        paymentsCounter = prometheus.getPaymentsCounter();
        paymentsCounter.increment();

        Payment payment = new Payment(paymentRequest.getAmount(), paymentRequest.getBeneficiary(), paymentRequest.getPayee());
        Payment savedPayment = paymentService.create(payment);

        if (savedPayment.getStatus().equals("success")) {
            paymentSuccessCounter = prometheus.getPaymentSuccessCounter();
            paymentSuccessCounter.increment();
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            paymentRequestTime.set(executeTime);
        }
        new Event("PAYMENTSUCCESSFUL", null, logger)
                .addProperty("PaymentId", String.valueOf(savedPayment.getId()))
                .addProperty("BeneficiaryIfscCode", savedPayment.getBeneficiaryIfscCode())
                .addProperty("PayeeIfscCode", savedPayment.getPayeeIfscCode())
                .publish();

        PaymentResponse response = new PaymentResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(savedPayment.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
