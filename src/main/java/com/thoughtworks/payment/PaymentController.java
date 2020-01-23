package com.thoughtworks.payment;

import com.google.gson.JsonObject;
import com.thoughtworks.payment.message.PaymentResponse;
import com.thoughtworks.payment.model.Payment;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.v;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    MeterRegistry meterRegistry;

    private Counter counter;

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

        counter = Counter
                .builder("paymentService")
                .description("a simple counter")
                .tags("counter", "counter")
                .register(meterRegistry);
        counter.increment();

        Payment payment = new Payment(paymentRequest.getAmount(), paymentRequest.getBeneficiary(), paymentRequest.getPayee());
        Payment savedPayment = paymentService.create(payment);

        JsonObject logDetails = new JsonObject();
        logDetails.addProperty("PaymentId", savedPayment.getId());
        logDetails.addProperty("BeneficiaryIfscCode", savedPayment.getBeneficiaryIfscCode());
        logDetails.addProperty("PayeeIfscCode", savedPayment.getPayeeIfscCode());
        logger.info("{name:{},details:{}}", v("name", "PAYMENTSUCCESSFUL"), v("details", logDetails));

        PaymentResponse response = new PaymentResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(savedPayment.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
