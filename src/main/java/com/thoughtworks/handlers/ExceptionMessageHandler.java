package com.thoughtworks.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.api.api.model.PaymentFailureResponse;
import com.thoughtworks.exceptions.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.v;

//@Hidden
@ControllerAdvice
public class ExceptionMessageHandler {
    private static Logger logger = LogManager.getLogger(ExceptionMessageHandler.class);

    private void logException(Exception ex) {
        ObjectNode mapperOne = new ObjectMapper().createObjectNode();
        mapperOne.put("stackTrace", Arrays.toString(ex.getStackTrace()));

        String eventCode;
        String descriptionString;
        String detailsString = "";
        String exceptionString;

        if (ServiceException.class.isAssignableFrom(ex.getClass())) {
            ServiceException serviceException = (ServiceException) ex;

            eventCode = serviceException.getErrorCode();
            descriptionString = serviceException.getErrorMessage();
            if (serviceException.getDetails() != null) {
                detailsString = serviceException.getDetails().toString();
            }
            exceptionString = serviceException.getClass().toString();

        } else {
            eventCode = "SYSTEM_ERROR";
            descriptionString = ex.getMessage();
            exceptionString = ex.toString();
        }

        logger.error("{eventCode:{},description:{},details:{},exception:{},stackTrace:{}}", v("eventCode", eventCode), v("description", descriptionString), v("details", detailsString), v("exception", exceptionString), v("stackTrace", ex.getStackTrace()));

        Throwable causedByException = ex.getCause();
        if ((causedByException) != null) {

            descriptionString = causedByException.getMessage();
            detailsString = "";
            exceptionString = causedByException.toString();
            logger.error("{eventCode:{},description:{},details:{},exception:{},stackTrace:{}}", v("eventCode", eventCode), v("description", descriptionString), v("details", detailsString), v("exception", exceptionString), v("stackTrace", ex.getStackTrace()));
        }
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected PaymentFailureResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getErrorCode(), ex.getErrorMessage());
        logException(ex);
        return new PaymentFailureResponse().message("MISSING_INFO").reasons(errors);

    }

    @ExceptionHandler(ResourceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected PaymentFailureResponse handleConflictException(ResourceConflictException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getErrorCode(), ex.getErrorMessage());
        logException(ex);
        return new PaymentFailureResponse().message("REQUEST_CONFLICT").reasons(errors);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected PaymentFailureResponse handleCircuitBreakException(CallNotPermittedException callNotPermittedException) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Could not process the request");
        logException(callNotPermittedException);
        return new PaymentFailureResponse().message("SERVER_ERROR").reasons(errors);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public PaymentFailureResponse handleArgumentValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        List<Map.Entry<String, String>> fieldErrorMessages = exception.getBindingResult().getFieldErrors().stream()
                .map(ExceptionMessageHandler::extractMessage)
                .collect(Collectors.toList());

        for (Map.Entry<String, String> errorMessage : fieldErrorMessages) {
            if (errors.containsKey(errorMessage.getKey())) {
                String updatedMsg = errors.get(errorMessage.getKey()) + "; " + errorMessage.getValue();
                errors.put(errorMessage.getKey(), updatedMsg);
            } else {
                errors.put(errorMessage.getKey(), errorMessage.getValue());
            }
        }

        logException(exception);
        return new PaymentFailureResponse().message("INVALID_INPUT").reasons(errors);
    }

    private static Map.Entry<String, String> extractMessage(FieldError fieldError) {
        return new HashMap.SimpleEntry<>(fieldError.getField(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected PaymentFailureResponse handleValidationException(ValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getErrorCode(), ex.getErrorMessage());
        logException(ex);
        return new PaymentFailureResponse().message("INVALID_INPUT").reasons(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected PaymentFailureResponse handleRequestNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Request body missing or incorrect format");
        logException(ex);
        return new PaymentFailureResponse().message("INVALID_INPUT").reasons(errors);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    protected PaymentFailureResponse handleProcessingException(BusinessException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getErrorCode(), ex.getErrorMessage());
        logException(ex);
        return new PaymentFailureResponse().message("REQUEST_UNPROCESSABLE").reasons(errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected PaymentFailureResponse handleGeneralException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Could not process the request");
        logException(ex);
        return new PaymentFailureResponse().message("SERVER_ERROR").reasons(errors);
    }
}
