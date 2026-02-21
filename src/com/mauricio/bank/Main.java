package com.mauricio.bank;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();

        bank.createAccount("001", "Mauricio", new BigDecimal("1000.00"));
        bank.createAccount("002", "Cliente", new BigDecimal("250.00"));

        bank.transfer("001", "002", new BigDecimal("150.00"));

        System.out.println("Saldo 001: " + bank.getAccount("001").getBalance());
        System.out.println("Saldo 002: " + bank.getAccount("002").getBalance());
    }
}
