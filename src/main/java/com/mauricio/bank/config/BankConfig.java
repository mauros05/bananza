package com.mauricio.bank.config;

import com.mauricio.bank.Bank;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankConfig {

    @Bean
    public Bank bank() {
        return new Bank();
    }
}
