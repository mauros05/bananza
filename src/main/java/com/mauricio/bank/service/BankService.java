package com.mauricio.bank.service;

import com.mauricio.bank.InsufficientFundsException;
import com.mauricio.bank.AccountNotFoundException;
import com.mauricio.bank.TransactionType;
import com.mauricio.bank.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BankService {
    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;

    public BankService(AccountRepository accountRepo, TransactionRepository txRepo) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
    }

    @Transactional
    public AccountEntity createAccount(String accountNumber, String ownerName, BigDecimal initialBalance) {
        validateAccountNumber(accountNumber);
        validateOwnerName(ownerName);

        BigDecimal init = normalizeInitialBalance(initialBalance);

        if (accountRepo.existsById(accountNumber)) {
            throw new IllegalArgumentException("Account already exists: " + accountNumber);
        }

        AccountEntity account = new AccountEntity(accountNumber, ownerName, init);
        accountRepo.save(account);

        record(accountNumber, TransactionType.ACCOUNT_CREATED, init,
                new BigDecimal("0.00"), init, "Account created");

        return account;
    }

    @Transactional(readOnly = true)
    public AccountEntity getAccount(String accountNumber) {
        return accountRepo.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Transactional
    public AccountEntity deposit(String accountNumber, BigDecimal amount) {
        BigDecimal normalized = normalizeMoney(amount);

        AccountEntity account = accountRepo.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(normalized);

        account.setBalance(after);
        accountRepo.save(account);

        record(accountNumber, TransactionType.DEPOSIT, normalized, before, after, "Deposit");
        return account;
    }

    @Transactional
    public AccountEntity withdraw(String accountNumber, BigDecimal amount) {
        BigDecimal normalized = normalizeMoney(amount);

        AccountEntity account = accountRepo.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        BigDecimal before = account.getBalance();
        if (before.compareTo(normalized) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Balance=" + before + ", amount=" + normalized);
        }

        BigDecimal after = before.subtract(normalized);
        account.setBalance(after);
        accountRepo.save(account);

        record(accountNumber, TransactionType.WITHDRAW, normalized, before, after, "Withdraw");
        return account;
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("from and to accounts must be different");
        }

        BigDecimal normalized = normalizeMoney(amount);

        // Orden para evitar deadlocks a nivel DB también
        String first = fromAccountNumber.compareTo(toAccountNumber) < 0 ? fromAccountNumber : toAccountNumber;
        String second = fromAccountNumber.compareTo(toAccountNumber) < 0 ? toAccountNumber : fromAccountNumber;

        AccountEntity firstAcc = accountRepo.findByAccountNumberForUpdate(first)
                .orElseThrow(() -> new AccountNotFoundException(first));
        AccountEntity secondAcc = accountRepo.findByAccountNumberForUpdate(second)
                .orElseThrow(() -> new AccountNotFoundException(second));

        AccountEntity from = fromAccountNumber.equals(first) ? firstAcc : secondAcc;
        AccountEntity to = toAccountNumber.equals(first) ? firstAcc : secondAcc;

        BigDecimal fromBefore = from.getBalance();
        if (fromBefore.compareTo(normalized) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Balance=" + fromBefore + ", amount=" + normalized);
        }
        BigDecimal fromAfter = fromBefore.subtract(normalized);
        from.setBalance(fromAfter);

        BigDecimal toBefore = to.getBalance();
        BigDecimal toAfter = toBefore.add(normalized);
        to.setBalance(toAfter);

        accountRepo.save(from);
        accountRepo.save(to);

        record(fromAccountNumber, TransactionType.TRANSFER_OUT, normalized, fromBefore, fromAfter,
                "Transfer to " + toAccountNumber);
        record(toAccountNumber, TransactionType.TRANSFER_IN, normalized, toBefore, toAfter,
                "Transfer from " + fromAccountNumber);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> getTransactions(String accountNumber, Pageable pageable) {
        // valida existencia
        if (!accountRepo.existsById(accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        return txRepo.findByAccountNumber(accountNumber, pageable);
    }

    // --- Helpers

    private void record(String accountNumber, TransactionType type, BigDecimal amount,
                        BigDecimal before, BigDecimal after, String description) {

        txRepo.save(new TransactionEntity(
                UUID.randomUUID(),
                accountNumber,
                type,
                amount,
                before,
                after,
                description,
                LocalDateTime.now()
        ));
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || !accountNumber.matches("\\d{3,20}")) {
            throw new IllegalArgumentException("Account number must be only digits, length 3-20");
        }
    }

    private void validateOwnerName(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name is required");
        }
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        if (value == null) throw new IllegalArgumentException("Amount is required");
        if (value.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be > 0");
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeInitialBalance(BigDecimal value) {
        if (value == null) throw new IllegalArgumentException("InitialBalance is required");
        if (value.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Initial balance must be >= 0");
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
