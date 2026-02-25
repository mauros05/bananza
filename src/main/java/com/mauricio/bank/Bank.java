package com.mauricio.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.UUID;

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

        BigDecimal normalizeInitial = normalizeInitialBalance(initialBalance);

        Account account = new Account(accountNumber, ownerName, normalizeInitial);
        accountsByNumber.put(accountNumber, account);
        transactionsByAccount.put(accountNumber, new ArrayList<>());


        record(accountNumber, TransactionType.ACCOUNT_CREATED, normalizeInitial, new BigDecimal("0.00"), normalizeInitial,"Account created");
        log.info(() -> "ACCOUNT_CREATED account=" + accountNumber + ", owner=" + ownerName + ", initialBalance=" + normalizeInitial);

        return account;
    }

    public Account getAccount(String accountNumber) {
        Account account = accountsByNumber.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }

    public void deposit(String accountNumber, BigDecimal amount){
        Account account = getAccount(accountNumber);
        BigDecimal normalized = normalizeMoney(amount);

        BigDecimal before = account.getBalance();
        account.deposit(normalized);
        BigDecimal after = account.getBalance();


        record(accountNumber, TransactionType.DEPOSIT, normalized, before, after,"Deposit");
        log.info(() -> "DEPOSIT account=" + accountNumber + " amount=" + normalized);
    }

    public void withdraw(String accountNumber, BigDecimal amount){
        Account account = getAccount(accountNumber);
        BigDecimal normalized = normalizeMoney(amount);

        BigDecimal before = account.getBalance();
        account.withdraw(normalized);
        BigDecimal after = account.getBalance();

        record(accountNumber, TransactionType.WITHDRAW, normalized, before, after,"Withdraw");
        log.info(() -> "WITHDRAW account=" + accountNumber + " amount=" + normalized);
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("from and to accounts must be different");
        }

        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);
        BigDecimal normalized = normalizeMoney(amount);

        // Primero retiramos, si falla por fondos insuficientes no depositamos.

        BigDecimal fromBefore = from.getBalance();
        from.withdraw(normalized);
        BigDecimal fromAfter = from.getBalance();

        BigDecimal toBefore = to.getBalance();
        to.deposit(normalized);
        BigDecimal toAfter = to.getBalance();

        record(fromAccountNumber, TransactionType.TRANSFER_OUT, normalized, fromBefore, fromAfter,"Transfer to " + toAccountNumber);
        record(toAccountNumber, TransactionType.TRANSFER_IN, normalized, toBefore, toAfter,"Transfer from " + fromAccountNumber);

        log.info(() -> "TRANSFER from=" + fromAccountNumber + ", to=" + toAccountNumber + ", amount=" + normalized);
    }

    public List<Transaction> getTransactions(String accountNumber) {
        getAccount(accountNumber);
        return Collections.unmodifiableList(transactionsByAccount.get(accountNumber));
    }

    // --Helpers

    private void record(String accountNumber, TransactionType type,  BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, String description){
        transactionsByAccount.get(accountNumber).add(
          new Transaction(UUID.randomUUID(),
                          accountNumber,
                          type,
                          amount,
                          balanceBefore,
                          balanceAfter,
                          description,
                          LocalDateTime.now()
          )
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

    private BigDecimal normalizeInitialBalance(BigDecimal value){
        if (value == null) throw  new IllegalArgumentException("InitialBalance is required");
        if (value.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Initial balance must be greater or equal than 0");
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
