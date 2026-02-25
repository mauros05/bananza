package com.mauricio.bank;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import  static org.junit.jupiter.api.Assertions.*;

class BankTest {
    @Test
    void createAccount_createsAccountAndRecordsTransaction(){
        Bank bank   = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), bank.getAccount("001").getBalance());
        assertEquals(1, bank.getTransactions("001").size());
        assertEquals(TransactionType.ACCOUNT_CREATED, bank.getTransactions("001").getFirst().type());
    }

    @Test
    void withdraw_withoutEnoughFounds_throwException(){
        Bank bank = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("300.00"));

        assertThrows(InsufficientFundsException.class, () -> bank.withdraw("001", new BigDecimal("400.00")));
    }

    @Test
    void transfer_createsTwoTransactions() {
        Bank bank = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("400.00"));
        bank.createAccount("003", "Fanny", new BigDecimal("500.00"));

        bank.transfer("001", "003", new BigDecimal("300.00"));

        assertEquals(2, bank.getTransactions("001").size());
        assertEquals(2, bank.getTransactions("003").size());
    }

    @Test
    void createAccount_withInvalidAccountNumber_throwsException(){
        Bank bank = new Bank();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () ->bank.createAccount("001a",
                                            "Mauricio",
                                        new BigDecimal("100.00")));

        assertTrue(ex.getMessage().toLowerCase().contains("digits"));
    }

    @Test
    void deposit_withZeroOrNegativeAmount_throwsException(){
        Bank bank = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("10.00"));

        assertThrows(IllegalArgumentException.class, () -> bank.deposit("001", new BigDecimal("0.00")));
        assertThrows(IllegalArgumentException.class, () -> bank.deposit("001", new BigDecimal("-1.00")));
    }

    @Test
    void transfer_toSameAccount_throwsException(){
        Bank bank = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> bank.transfer("001", "001", new BigDecimal("10.00")));

    }

    @Test
    void transfer_updatesBalanceCorrectly(){
        Bank bank = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("100.00"));
        bank.createAccount("002", "Fanny", new BigDecimal("50.00"));

        bank.transfer("001", "002", new BigDecimal("20.00"));

        assertEquals(new BigDecimal("80.00"), bank.getAccount("001").getBalance());
        assertEquals(new BigDecimal("70.00"), bank.getAccount("002").getBalance());

    }

    @Test
    void deposit_normalizesMoneyToTwoDecimals(){
        Bank bank = new Bank();
        bank.createAccount("001", "Mauricio", new BigDecimal("0.00"));

        bank.deposit("001", new BigDecimal("10.1"));

        assertEquals(new BigDecimal("10.10"), bank.getAccount("001").getBalance());
    }

    @Test
    void concurrentTransfers_doNotLoseMoney() throws Exception {
        Bank bank = new Bank();
        bank.createAccount("001", "A", new BigDecimal("1000.00"));
        bank.createAccount("002", "B", new BigDecimal("1000.00"));

        int threads = 20;
        int transferPerThread = 200;

        var executor = java.util.concurrent.Executors.newFixedThreadPool(threads);

        for (int t = 0; t < threads; t ++){
            executor.submit(() -> {
                for (int i = 0; i < transferPerThread; i ++){
                    bank.transfer("001", "002", new BigDecimal("1.00"));
                    bank.transfer("002", "001", new BigDecimal("1.00"));
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(20, TimeUnit.SECONDS);
        assertTrue(finished);

        BigDecimal total = bank.getAccount("001").getBalance().add(bank.getAccount("002").getBalance());
        assertEquals(new BigDecimal("2000.00"), total);
    }



}
