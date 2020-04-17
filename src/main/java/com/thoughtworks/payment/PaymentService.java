package com.thoughtworks.payment;

import com.google.gson.JsonObject;
import com.thoughtworks.ErrorCodes.InternalErrorCodes;
import com.thoughtworks.api.api.model.PaymentRequest;
import com.thoughtworks.exceptions.PaymentRefusedException;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import com.thoughtworks.serviceclients.BankClient;
import com.thoughtworks.serviceclients.FraudClient;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankClient bankClient;

    @Autowired
    FraudClient fraudClient;

    @Autowired
    Prometheus prometheus;

    private Counter bankCounter;

    public Payment create(@Valid PaymentRequest paymentRequest) throws Exception {
        if (paymentRequest == null) {
            throw new IllegalArgumentException("paymentRequest cannot be null");
        }
        Payment payment = new Payment(paymentRequest.getAmount(), paymentRequest.getBeneficiary(), paymentRequest.getPayee());
        boolean isValidBeneficiaryAccount = bankClient.checkBankDetails(payment.getBeneficiaryAccountNumber(), payment.getBeneficiaryIfscCode());
        if (!isValidBeneficiaryAccount) {
            payment.setStatus("failed");
            paymentRepository.save(payment);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("PaymentId", String.valueOf(payment.getId()));
            jsonObject.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            jsonObject.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());

            throw new ResourceNotFoundException(InternalErrorCodes.ACCOUNT_NOT_FOUND, payment.getBeneficiaryName() + "'s AccountDetails Not Found At " + payment.getBeneficiaryIfscCode(), jsonObject);
        }
        boolean isValidPayeeAccount = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (!isValidPayeeAccount) {
            payment.setStatus("failed");
            paymentRepository.save(payment);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("PaymentId", String.valueOf(payment.getId()));
            jsonObject.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            jsonObject.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());

            throw new ResourceNotFoundException(InternalErrorCodes.ACCOUNT_NOT_FOUND, payment.getPayeeName() + "'s AccountDetails Not Found At " + payment.getPayeeIfscCode(), jsonObject);
        }

        boolean isValidPayment = fraudClient.checkFraud(payment);
        if (!isValidPayment) {
            payment.setStatus("failed");
            paymentRepository.save(payment);
            throw new PaymentRefusedException(InternalErrorCodes.SUSPECTED_ACCOUNT, "Suspected fraudulent transaction");
        }

        bankCounter = prometheus.getBankInfoCounter(payment.getBeneficiaryIfscCode().substring(0, 4));
        bankCounter.increment();
        return paymentRepository.save(payment);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

}
