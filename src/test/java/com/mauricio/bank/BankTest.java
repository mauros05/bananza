package com.mauricio.bank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import  static org.junit.jupiter.api.Assertions.*;

class BankTest {
    @Test
    void createAccount_createsAccountAndRecordsTransaction(){
        Bank bank   = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), bank.getAccount("001").getBalance());
        assertEquals(1, bank.getTransactions("001").size());
        assertEquals(TransactionType.ACCOUNT_CREATED, bank.getTransactions("001").get(0).type());
    }
}
