package com.mauricio.bank;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        ConsoleApp app = new ConsoleApp(bank);
        app.run();
    }
}
