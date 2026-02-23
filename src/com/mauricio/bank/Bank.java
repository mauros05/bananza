package com.mauricio.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class Bank {
    private static final Logger log = Logger.getLogger(Bank.class.getName());

    private final Map<String, Account> accountsByNumber = new HashMap<>();
    private final Map<String, List<Transaction>> transactionsByAccount = new HashMap<>();

    public Account createAccount(String accountNumber, String ownerName, BigDecimal initialBalance) {
        validateAccountNumber(accountNumber);
        validateOwnerName(ownerName);

        if (accountsByNumber.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account already exists: " + accountNumber);
        }

        Account account = new Account(accountNumber, ownerName, initialBalance);
        accountsByNumber.put(accountNumber, account);
        transactionsByAccount.put(accountNumber, new ArrayList<>());


        record(accountNumber, TransactionType.ACCOUNT_CREATED, initialBalance, "Account created");
        log.info(() -> "ACCOUNT_CREATED account=" + accountNumber + ", owner=" + ownerName + ", initialBalance=" + initialBalance);

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


    //    Helpers

    private void record(String accountNumber, TransactionType type,  BigDecimal amount, String description){
        transactionsByAccount.get(accountNumber).add(
          new Transaction(accountNumber, type, amount, description, LocalDateTime.now())
        );
    }

    private void validateAccountNumber(String accountNumber) {
        if(accountNumber == null || !accountNumber.matches("\\d{3,20}")){
            throw new IllegalArgumentException("Account number must be only digits, length 3-20");
        }
    }

    private void validateOwnerName(String ownerName) {
        if (ownerName == null || ownerName.isBlank()){
            throw new IllegalArgumentException("Owner name is required");
        }
    }

    private BigDecimal normalizeMoney(BigDecimal value){
        if (value == null) throw new IllegalArgumentException("Amount is required");
        if (value.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be > 0");

        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
