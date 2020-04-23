package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.ErrorCodes.EventCodes;
import com.thoughtworks.api.api.PaymentsApi;
import com.thoughtworks.api.api.model.Payment;
import com.thoughtworks.api.api.model.PaymentRequest;
import com.thoughtworks.api.api.model.PaymentSuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentsApi {
    @Autowired
    private PaymentService paymentService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @PostMapping(consumes = {"application/json"})
    @Override
    public ResponseEntity<PaymentSuccessResponse> create1(@Valid @RequestBody PaymentRequest paymentRequest) throws Exception {
        com.thoughtworks.payment.model.Payment savedPayment = paymentService.create(paymentRequest);
        ObjectNode mapper = new ObjectMapper().createObjectNode();
        mapper.put("PaymentId", String.valueOf(savedPayment.getId()));
        mapper.put("BeneficiaryIfscCode", savedPayment.getBeneficiaryIfscCode());
        mapper.put("PayeeIfscCode", savedPayment.getPayeeIfscCode());
        log.info("payment successful", kv("eventCode", EventCodes.PAYMENT_SUCCESSFUL), kv("details", mapper.toString()));
        PaymentSuccessResponse response = new PaymentSuccessResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(savedPayment.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Payment>> getAllPayments() throws Exception {
        List paymentList = paymentService.getAll();
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentList);
    }

}
