package com.thoughtworks.payment;

import com.google.gson.JsonObject;
import com.thoughtworks.bankclient.BankClient;
import com.thoughtworks.payment.model.Payment;
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

    public Payment create(Payment payment) throws Exception {
        int beneficiaryResponseCode = bankClient.checkBankDetails(payment.getBeneficiaryAccountNumber(), payment.getBeneficiaryIfscCode());
        if (beneficiaryResponseCode == 404) {
            payment.setStatus("failed");
            paymentRepository.save(payment);

            JsonObject logDetails = new JsonObject();
            logDetails.addProperty("PaymentId", payment.getId());
            logDetails.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            logDetails.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());
            logger.info("{name:{},description:{},details:{}}", v("name", "PAYMENTFAILED"), v("description", "Beneficiary details are incorrect"), v("details", logDetails));

            throw new BeneficiaryAccountDetailsNotFound("message", payment.getBeneficiaryName() + "'s AccountDetails Not Found");
        }
        int payeeResponseCode = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (payeeResponseCode == 404) {
            payment.setStatus("failed");
            paymentRepository.save(payment);

            JsonObject logDetails = new JsonObject();
            logDetails.addProperty("PaymentId", payment.getId());
            logDetails.addProperty("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            logDetails.addProperty("PayeeIfscCode", payment.getPayeeIfscCode());
            logger.info("{name:{},description:{},details:{}}", v("name", "PAYMENTFAILED"), v("description", "Payee details are incorrect"), v("details", logDetails));

            throw new PayeeAccountDetailsNotFound("message", payment.getPayeeName() + "'s AccountDetails Not Found");
        }
        return paymentRepository.save(payment);
    }

}
