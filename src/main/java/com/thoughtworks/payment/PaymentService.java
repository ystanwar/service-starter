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
        paymentFailed = prometheus.getPaymentFailedCounter();

        int beneficiaryResponseCode = bankClient.checkBankDetails(payment.getBeneficiaryAccountNumber(), payment.getBeneficiaryIfscCode());
        if (beneficiaryResponseCode == 404) {
            payment.setStatus("failed");
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment.getStatus().equals("failed")) {
                paymentFailed.increment();
            }

            ErrorEvent errorEvent = new ErrorEvent("PAYMENTFAILED", payment.getBeneficiaryName() + "'s details are incorrect", logger);
            errorEvent.addProperty("PaymentId", String.valueOf(payment.getId()));
            errorEvent.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            errorEvent.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());
            errorEvent.publish();

            throw new BeneficiaryAccountDetailsNotFound("message", payment.getBeneficiaryName() + "'s AccountDetails Not Found");
        }
        int payeeResponseCode = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (payeeResponseCode == 404) {
            payment.setStatus("failed");
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment.getStatus().equals("failed")) {
                paymentFailed.increment();
            }

            ErrorEvent errorEvent = new ErrorEvent("PAYMENTFAILED", payment.getPayeeName() + "'s details are incorrect", logger);
            errorEvent.addProperty("PaymentId", String.valueOf(payment.getId()));
            errorEvent.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            errorEvent.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());
            errorEvent.publish();

            throw new PayeeAccountDetailsNotFound("message", payment.getPayeeName() + "'s AccountDetails Not Found");
        }
        return paymentRepository.save(payment);
    }

}
