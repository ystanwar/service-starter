package com.thoughtworks.payment;

import com.thoughtworks.bankclient.BankClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankDetailsRepository bankDetailsRepository;

    @Autowired
    BankClient bankClient;

    public Payment create(Payment payment) throws Exception {
        BankDetails beneficiary = payment.getBeneficiary();
        int beneficiaryResponseCode = bankClient.checkBankDetails(beneficiary.getAccountNumber(), beneficiary.getIfscCode());
        if (beneficiaryResponseCode == 404) {
            throw new BeneficiaryAccountDetailsNotFound("Beneficiary AccountDetails Not Found");
        }
        BankDetails payee = payment.getPayee();
        int payeeResponseCode = bankClient.checkBankDetails(payee.getAccountNumber(), payee.getIfscCode());
        if (payeeResponseCode == 404) {
            throw new PayeeAccountDetailsNotFound("Payee AccountDetails Not Found");
        }
        bankDetailsRepository.save(payment.getBeneficiary());
        bankDetailsRepository.save(payment.getPayee());
        return paymentRepository.save(payment);
    }

}
