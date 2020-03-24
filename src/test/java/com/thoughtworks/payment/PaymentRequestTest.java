package com.thoughtworks.payment;

import com.thoughtworks.api.api.model.*;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PaymentRequestTest {

    @Test
    public void createPaymentWithAmountGreaterThanLimit() throws Exception {

        BankDetails payee = new BankDetails().name("user2").accountNumber(67890L).ifscCode("HDFC1234");

        Set<ConstraintViolation<PaymentRequest>> violations = CheckPaymentRequest(new PaymentRequest().amount(10000000).beneficiary(null).payee(payee));

        ConstraintViolation<PaymentRequest> amountLimitViolation = null;
        ConstraintViolation<PaymentRequest> beneficiaryNullViolation = null;

        for (ConstraintViolation<PaymentRequest> violation : violations) {
            if (String.valueOf(violation.getPropertyPath()).equals("amount")) amountLimitViolation = violation;
            if (String.valueOf(violation.getPropertyPath()).equals("beneficiary")) beneficiaryNullViolation = violation;
        }
        assertNotNull(amountLimitViolation);
        assertNotNull(beneficiaryNullViolation);
    //     assertEquals("amount cannot be greater than 100000", amountLimitViolation.getMessage());
    //     assertEquals("beneficiary info cannot be null", beneficiaryNullViolation.getMessage());
     }

    private Set<ConstraintViolation<PaymentRequest>> CheckPaymentRequest(PaymentRequest paymentRequest) throws Exception {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        return validator.validate(paymentRequest);
    }

}
