package com.thoughtworks.handler;

import com.thoughtworks.bankInfo.BankInfoAlreadyExistsException;
import com.thoughtworks.error.BankInfoErrorResponse;
import com.thoughtworks.error.PaymentErrorResponse;
import com.thoughtworks.payment.BeneficiaryAccountDetailsNotFound;
import com.thoughtworks.payment.PayeeAccountDetailsNotFound;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionMessageHandler {

    @ExceptionHandler(BeneficiaryAccountDetailsNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected PaymentErrorResponse InvalidBeneficiaryAccount(BeneficiaryAccountDetailsNotFound ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getKey(), ex.getValue());
        return new PaymentErrorResponse("PAYMENT_FAILED", errors);
    }

    @ExceptionHandler(PayeeAccountDetailsNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected PaymentErrorResponse InvalidPayeeAccount(PayeeAccountDetailsNotFound ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getKey(), ex.getValue());
        return new PaymentErrorResponse("PAYMENT_FAILED", errors);
    }

    @ExceptionHandler(BankInfoAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected BankInfoErrorResponse bankInfoAlreadyExists(BankInfoAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getKey(), ex.getValue());
        return new BankInfoErrorResponse("Bank info already exists", errors);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseBody
    protected String CircuitBreakerException(CallNotPermittedException CallNotPermittedException) {
        return "CircuitBreakerException";
    }
}
