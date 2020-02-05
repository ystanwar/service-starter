package com.thoughtworks.payment;

import com.thoughtworks.bankclient.BankClient;
import com.thoughtworks.logger.ErrorEvent;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private static Logger logger = LogManager.getLogger(PaymentController.class);
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankClient bankClient;

    @Autowired
    MeterRegistry meterRegistry;

    @Autowired
    Prometheus prometheus;

    private Counter paymentFailed;

    public Payment create(Payment payment) throws Exception {
        if (payment == null) {
            throw new IllegalArgumentException("payment cannot be empty");
        }
        paymentFailed = prometheus.getPaymentFailedCounter();

        boolean isValidBeneficiaryAccount = bankClient.checkBankDetails(payment.getBeneficiaryAccountNumber(), payment.getBeneficiaryIfscCode());
        if (!isValidBeneficiaryAccount) {
            payment.setStatus("failed");
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment.getStatus().equals("failed")) {
                paymentFailed.increment();
            }

            new ErrorEvent("PAYMENTFAILED", payment.getBeneficiaryName() + "'s details are incorrect", logger)
                    .addProperty("PaymentId", String.valueOf(payment.getId()))
                    .addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode())
                    .addProperty("PayeeIfscCode", payment.getPayeeIfscCode())
                    .publish();

            throw new BeneficiaryAccountDetailsNotFound("message", payment.getBeneficiaryName() + "'s AccountDetails Not Found");
        }
        boolean isValidPayeeAccount = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (!isValidPayeeAccount) {
            payment.setStatus("failed");
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment.getStatus().equals("failed")) {
                paymentFailed.increment();
            }

            new ErrorEvent("PAYMENTFAILED", payment.getPayeeName() + "'s details are incorrect", logger)
                    .addProperty("PaymentId", String.valueOf(payment.getId()))
                    .addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode())
                    .addProperty("PayeeIfscCode", payment.getPayeeIfscCode())
                    .publish();

            throw new PayeeAccountDetailsNotFound("message", payment.getPayeeName() + "'s AccountDetails Not Found");
        }

        return paymentRepository.save(payment);
    }

}
