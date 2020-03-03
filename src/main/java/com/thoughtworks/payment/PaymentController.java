package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.api.payment.PaymentRequest;
import com.thoughtworks.api.payment.PaymentSuccessResponse;
import com.thoughtworks.api.payment.PaymentsApiController;
import com.thoughtworks.payment.model.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.v;

@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentsApiController {
    private static Logger logger = LogManager.getLogger(PaymentController.class);

    @Autowired
    PaymentService paymentService;

    @Autowired
    Environment environment;

    @PostMapping(consumes = { "application/json"})
    @Override
    public ResponseEntity<PaymentSuccessResponse> create(@Valid @RequestBody PaymentRequest paymentRequest) throws Exception {
        Payment payment = new Payment(paymentRequest.getAmount(), paymentRequest.getBeneficiary(), paymentRequest.getPayee());
        Payment savedPayment = paymentService.create(payment);

        ObjectNode mapper = new ObjectMapper().createObjectNode();
        mapper.put("PaymentId", String.valueOf(savedPayment.getId()));
        mapper.put("BeneficiaryIfscCode", savedPayment.getBeneficiaryIfscCode());
        mapper.put("PayeeIfscCode", savedPayment.getPayeeIfscCode());

        logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{}}", v("name", "PAYMENT_SUCCESSFUL"), v("description", "payment successful"), v("details", mapper.toString()));

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
