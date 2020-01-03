package com.thoughtworks.payment;

import com.thoughtworks.bankclient.BankClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankClient bankClient;

    public Payment create(Payment payment) throws Exception {
        int beneficiaryResponseCode = bankClient.checkBankDetails(payment.getBeneficiaryAccountNumber(), payment.getBeneficiaryIfscCode());
        if (beneficiaryResponseCode == 404) {
            throw new BeneficiaryAccountDetailsNotFound("message", payment.getBeneficiaryName() + "'s AccountDetails Not Found");
        }
        int payeeResponseCode = bankClient.checkBankDetails(payment.getPayeeAccountNumber(), payment.getPayeeIfscCode());
        if (payeeResponseCode == 404) {
            throw new PayeeAccountDetailsNotFound("message", payment.getPayeeName() + "'s AccountDetails Not Found");
        }

        return paymentRepository.save(payment);
    }

}
