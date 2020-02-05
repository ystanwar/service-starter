package com.thoughtworks.handler;

import com.thoughtworks.bankInfo.BankInfoAlreadyExistsException;
import com.thoughtworks.error.BankInfoErrorResponse;
import com.thoughtworks.error.PaymentErrorResponse;
import com.thoughtworks.error.ValidationErrorResponse;
import com.thoughtworks.payment.BeneficiaryAccountDetailsNotFound;
import com.thoughtworks.payment.PayeeAccountDetailsNotFound;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse handleException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        List<Map.Entry<String, String>> fieldErrorMessages = exception.getBindingResult().getFieldErrors().stream()
                .map(ExceptionMessageHandler::ExtractMessage)
                .collect(Collectors.toList());

        for(Map.Entry<String, String> errorMessage : fieldErrorMessages){
            if(errors.containsKey(errorMessage.getKey())){
                String updatedMsg = errors.get(errorMessage.getKey()) + "; " + errorMessage.getValue();
                errors.put(errorMessage.getKey(), updatedMsg);
            }else {
                errors.put(errorMessage.getKey(), errorMessage.getValue());
            }
        }
        return new ValidationErrorResponse("Request validation failure", errors);
    }

    public static Map.Entry<String, String> ExtractMessage(FieldError fieldError) {
            return new HashMap.SimpleEntry<>(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
