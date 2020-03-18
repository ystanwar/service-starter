package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.api.api.PaymentsApi;
import com.thoughtworks.api.api.model.Payment;
import com.thoughtworks.api.api.model.PaymentRequest;
import com.thoughtworks.api.api.model.PaymentSuccessResponse;
import com.thoughtworks.payment.model.BankDetails;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.v;

@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentsApi {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @PostMapping(consumes = {"application/json"})
    @Override
    public ResponseEntity<PaymentSuccessResponse> create1(@Valid PaymentRequest paymentRequest) throws Exception {
        BankDetails beneficiary = new BankDetails(paymentRequest.getBeneficiary().getName(), paymentRequest.getBeneficiary().getAccountNumber(), paymentRequest.getBeneficiary().getIfscCode());
        BankDetails payee = new BankDetails(paymentRequest.getPayee().getName(), paymentRequest.getPayee().getAccountNumber(), paymentRequest.getPayee().getIfscCode());
        com.thoughtworks.api.payment.PaymentRequest paymentRequest1 = new com.thoughtworks.api.payment.PaymentRequest(paymentRequest.getAmount(), beneficiary, payee);
        com.thoughtworks.payment.model.Payment savedPayment = paymentService.create(paymentRequest1);

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
    public ResponseEntity<List<Payment>> getAllPayments() throws Exception {
        List paymentList = paymentService.getAll();
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentList);
    }

}
