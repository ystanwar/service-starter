package com.thoughtworks.api.bankinfo;

import com.thoughtworks.api.payment.PaymentFailureResponse;
import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.exceptions.ResourceConflictException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "BankInfos", description = "Manage the banks available and their urls")
public interface BankInfoApiController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "BankInfo created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = PaymentFailureResponse.class)))
    })
    @Operation(summary = "Add a bankinfo ", description = "Adds the new bank and its url", tags = { "BankInfos" })
    public BankInfo create(@RequestBody BankInfo bankInfo) throws ResourceConflictException;
}
