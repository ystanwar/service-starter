package com.thoughtworks.payment;

import com.google.gson.JsonObject;
import com.thoughtworks.bankclient.BankClient;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static net.logstash.logback.argument.StructuredArguments.v;

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
            JsonObject logDetails = new JsonObject();
            logDetails.addProperty("PaymentId", payment.getId());
            logDetails.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            logDetails.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());
            logger.info("{name:{},description:{},details:{}}", v("name", "PAYMENTFAILED"), v("description", payment.getBeneficiaryName() + "details are incorrect"), v("details", logDetails));

            throw new BeneficiaryAccountDetailsNotFound("message", payment.getBeneficiaryName() + "'s AccountDetails Not Found");
        }
        int payeeResponseCode = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (payeeResponseCode == 404) {
            payment.setStatus("failed");
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment.getStatus().equals("failed")) {
                paymentFailed.increment();
            }
            JsonObject logDetails = new JsonObject();
            logDetails.addProperty("PaymentId", payment.getId());
            logDetails.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            logDetails.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());
            logger.info("{name:{},description:{},details:{}}", v("name", "PAYMENTFAILED"), v("description", payment.getPayeeName() + "details are incorrect"), v("details", logDetails));

            throw new PayeeAccountDetailsNotFound("message", payment.getPayeeName() + "'s AccountDetails Not Found");
        }
        return paymentRepository.save(payment);
    }

}
