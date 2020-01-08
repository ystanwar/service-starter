package com.thoughtworks.bankInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankInfoRepository extends JpaRepository<BankInfo, Integer> {
    BankInfo findByBankCode(String bankCode);
}
