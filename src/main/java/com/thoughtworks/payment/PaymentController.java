package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.api.payment.PaymentRequest;
import com.thoughtworks.api.payment.PaymentSuccessResponse;
import com.thoughtworks.api.payment.PaymentsApiController;
import com.thoughtworks.payment.model.Payment;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.v;

@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentsApiController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @PostMapping(consumes = {"application/json"})
    @Override
    public ResponseEntity<PaymentSuccessResponse> create(@Valid @RequestBody PaymentRequest paymentRequest) throws Exception {
        Payment savedPayment = paymentService.create(paymentRequest);

        ObjectNode mapper = new ObjectMapper().createObjectNode();
        mapper.put("PaymentId", String.valueOf(savedPayment.getId()));
        mapper.put("BeneficiaryIfscCode", savedPayment.getBeneficiaryIfscCode());
        mapper.put("PayeeIfscCode", savedPayment.getPayeeIfscCode());

        logger.info("{eventCode:{},description:{},details:{}}", v("name", "PAYMENT_SUCCESSFUL"), v("description", "payment successful"), v("details", mapper.toString()));

        PaymentSuccessResponse response = new PaymentSuccessResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(savedPayment.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public List<Payment> getAllPayments() {
        return paymentService.getAll();
    }
}
