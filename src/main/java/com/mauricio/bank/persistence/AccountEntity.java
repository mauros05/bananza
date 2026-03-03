package com.mauricio.bank.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;


    @Version
    private long version;

    protected AccountEntity() {}

    public AccountEntity(String accountNumber, String ownerName, BigDecimal balance){
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
