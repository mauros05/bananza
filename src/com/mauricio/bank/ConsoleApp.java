package com.mauricio.bank;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleApp {
    private final Bank bank;
    private final Scanner scanner;


    public ConsoleApp(Bank bank){
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("=== Banking System Console ===");

        boolean running = true;

        while (running) {
            printMenu();
            String option = readLine("Seleccione una opcion: ");

            try {
                switch (option) {
                    case "1" -> createAccount();
                    case "2" -> deposit();
                    case "3" -> withdraw();
                }
            }

        }


    }

    private void createAccount(){
        String number = readLine("Numero de cuenta: ");
        String owner = readLine("Nombre del dueño: ");
        BigDecimal initialBalance = readMoney("Saldo inicial: ");

        bank.createAccount(number, owner, initialBalance);
        System.out.println("Cuenta creada");
    }

    private void deposit(){
        String number = readLine("Numero de cuenta: ");
        BigDecimal amount = readMoney("Cantidad a depositar: ");

        bank.getAccount(number).deposit(amount);
        System.out.println("Deposito realizado");
    }

    private void withdraw(){
        String number = readLine("Numero de cuenta: ");
        BigDecimal amount = readMoney("Cantidad a retirar: ");

        bank.getAccount(number).withdraw(amount);
        System.out.println("Retiro realizado");
    }

    private void printMenu(){
        System.out.println("""
                1) Crear cuenta
                2) Depositar
                3) Retirar
                4) Transferir
                5) Consultar saldo
                0) Salir
                """);
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private BigDecimal readMoney(String prompt){
        while (true){
            String input = readLine(prompt);

            try {
                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(BigDecimal.ZERO) <= 0){
                    System.out.println("El mmonto debe ser mayor a 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e){
                System.out.println("Formato invalido. Ejemplo valido: 150.00");
            }
        }
    }

}
