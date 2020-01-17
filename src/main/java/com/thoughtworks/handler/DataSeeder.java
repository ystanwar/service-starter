package com.thoughtworks.handler;

import com.thoughtworks.bankInfo.BankInfo;
import com.thoughtworks.bankInfo.BankInfoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initDatabase(BankInfoService bankService) {
        return args -> {
            bankService.create(new BankInfo("HDFC", "http://localhost:8082"));
            bankService.create(new BankInfo("AXIS", "http://localhost:8084"));
        };
    }


}
