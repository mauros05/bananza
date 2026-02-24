package com.mauricio.bank;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final String accountNumber;
    private final String ownerName;
    private BigDecimal balance;

    public Account(String accountNumber, String ownerName, BigDecimal initialBalance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber is required");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("ownerName is required");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("initialBalance must be >= 0");
        }

        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }

    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Balance=" + balance + ", amount=" + amount);
        }
        balance = balance.subtract(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
