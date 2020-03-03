package com.thoughtworks.api.payment;
import com.thoughtworks.payment.model.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Payments", description = "Manage the transactions between two users.")
public interface PaymentsApiController {

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "404", description = "BankAccount not found",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a payment ", description = "Make payemt between two users", tags = { "Payments" })
    public ResponseEntity<PaymentSuccessResponse> create(@Valid @RequestBody PaymentRequest paymentRequest) throws Exception;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Payments gets successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class))),
    })
    @Operation(summary = "Gets all payments ", description = "Gets All Transactions done ", tags = { "Payments" })
    @GetMapping
    public List<Payment> getAllPayments();
}