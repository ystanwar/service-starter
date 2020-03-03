package com.thoughtworks.bankInfo;

import com.thoughtworks.api.PaymentServiceURLs;
import com.thoughtworks.exceptions.ResourceConflictException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankinfo")
@Tag(name = "BankInfos", description = "Manage the banks available and their urls")
public class BankInfoController {

    @Autowired
    BankInfoService bankInfoService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "BankInfo created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping(consumes = { "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a bankinfo ", description = "Adds the new bank and its url", tags = { "BankInfos" })
    public BankInfo create(@RequestBody BankInfo bankInfo) throws ResourceConflictException {
        return bankInfoService.create(bankInfo);
    }
}
