package com.thoughtworks;

import com.thoughtworks.bank.Bank;
import com.thoughtworks.bank.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initDatabase(BankService bankService) {
        return args -> {
            bankService.create(new Bank("HDFC", "http://localhost:8082"));
            bankService.create(new Bank("AXIS", "http://localhost:8084"));
        };
    }


}
