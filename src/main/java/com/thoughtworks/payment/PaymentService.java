package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.exceptions.ResourceNotFoundException;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.serviceclients.BankClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.v;

@Service
public class PaymentService {
    private static Logger logger = LogManager.getLogger(PaymentController.class);
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankClient bankClient;

    public Payment create(Payment payment) throws Exception {
        if (payment == null) {
            throw new IllegalArgumentException("payment cannot be empty");
        }


        boolean isValidBeneficiaryAccount = bankClient.checkBankDetails(payment.getBeneficiaryAccountNumber(), payment.getBeneficiaryIfscCode());
        if (!isValidBeneficiaryAccount) {
            payment.setStatus("failed");
            paymentRepository.save(payment);

            ObjectNode mapper = new ObjectMapper().createObjectNode();
            mapper.put("PaymentId", String.valueOf(payment.getId()));
            mapper.put("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            mapper.put("PayeeIfscCode", payment.getPayeeIfscCode());
            logger.error("{name:{},description:{},details:{}}", v("name", "PAYMENTFAILED"), v("description", payment.getBeneficiaryName() + "'s details are incorrect"), v("details", mapper.toString()));

            throw new ResourceNotFoundException("message", payment.getBeneficiaryName() + "'s AccountDetails Not Found");
        }
        boolean isValidPayeeAccount = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (!isValidPayeeAccount) {
            payment.setStatus("failed");
            paymentRepository.save(payment);

            ObjectNode mapper = new ObjectMapper().createObjectNode();
            mapper.put("PaymentId", String.valueOf(payment.getId()));
            mapper.put("BeneficiaryIfscCode", payment.getBeneficiaryIfscCode());
            mapper.put("PayeeIfscCode", payment.getPayeeIfscCode());
            logger.error("{name:{},description:{},details:{}}", v("name", "PAYMENTFAILED"), v("description", payment.getPayeeName() + "'s details are incorrect"), v("details", mapper.toString()));

            throw new ResourceNotFoundException("message", payment.getPayeeName() + "'s AccountDetails Not Found");
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

}
