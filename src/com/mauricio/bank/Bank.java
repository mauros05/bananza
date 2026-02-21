package com.mauricio.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accountsByNumber = new HashMap<>();

    public Account createAccount(String accountNumber, String ownerName, BigDecimal initialBalance) {
        if (accountsByNumber.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account already exists: " + accountNumber);
        }
        Account account = new Account(accountNumber, ownerName, initialBalance);
        accountsByNumber.put(accountNumber, account);
        log("ACCOUNT_CREATED", "account=" + accountNumber + ", owner=" + ownerName + ", initialBalance=" + initialBalance);
        return account;
    }

    public Account getAccount(String accountNumber) {
        Account account = accountsByNumber.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("from and to accounts must be different");
        }

        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);

        // Primero retiramos, si falla por fondos insuficientes no depositamos.
        from.withdraw(amount);
        to.deposit(amount);

        log("TRANSFER", "from=" + fromAccountNumber + ", to=" + toAccountNumber + ", amount=" + amount);
    }

    private void log(String event, String message) {
        System.out.println(LocalDateTime.now() + " [" + event + "] " + message);
    }
}
