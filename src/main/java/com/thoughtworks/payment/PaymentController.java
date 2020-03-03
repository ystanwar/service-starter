package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.api.payment.PaymentFailureResponse;
import com.thoughtworks.api.payment.PaymentRequest;
import com.thoughtworks.api.payment.PaymentSuccessResponse;
import com.thoughtworks.payment.model.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payments", description = "Manage the transactions between two users.")
public class PaymentController {
    private static Logger logger = LogManager.getLogger(PaymentController.class);

    @Autowired
    PaymentService paymentService;

    @Autowired
    Environment environment;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "404", description = "BankAccount not found",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class)))
    })
    @PostMapping(consumes = { "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a payment ", description = "Make payemt between two users", tags = { "Payments" })
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


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Payments gets successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class))),
    })
    @Operation(summary = "Gets all payments ", description = "Gets All Transactions done ", tags = { "Payments" })
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAll();
    }
}
