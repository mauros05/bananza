package com.mauricio.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                    case "4" -> transfer();
                    case "5" -> showBalance();
                    case "6" -> showHistory();
                    case "0" -> running = false;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println();
        }
        System.out.println("Hasta luego");
    }

    private void createAccount(){
        String number = readAccountNumber("Numero de cuenta: ");
        String owner = readOwnerName("Nombre del dueño: ");
        BigDecimal initialBalance = readMoneyAllowZero("Saldo inicial: ");

        bank.createAccount(number, owner, initialBalance);
        System.out.println("Cuenta creada");
    }

    private void deposit(){
        String number = readAccountNumber("Numero de cuenta: ");
        BigDecimal amount = readMoneyPositive("Cantidad a depositar: ");

        bank.deposit(number,amount);
        System.out.println("Deposito realizado");
    }

    private void withdraw(){
        String number = readAccountNumber("Numero de cuenta: ");
        BigDecimal amount = readMoneyPositive("Cantidad a retirar: ");

        bank.withdraw(number,amount);
        System.out.println("Retiro realizado");
    }

    private void transfer(){
        String from = readAccountNumber("Numero de cuenta de origen: ");
        String to = readAccountNumber("Numero de cuenta destino: ");
        BigDecimal amount = readMoneyPositive("Cantidad a transferir: ");

        bank.transfer(from, to, amount);
        System.out.println("Transferencia realizada con exito");
    }

    private void showBalance(){
        String number = readAccountNumber("Numero de cuenta: ");
        System.out.println("Saldo actual: " + bank.getAccount(number).getBalance());
    }

    private void showHistory(){
        String number = readAccountNumber("Numero de cuenta: ");
        var txs = bank.getTransactions(number);

        if (txs.isEmpty()){
            System.out.println("No hay transacciones.");
            return;
        }

        System.out.println("=== Historial de " + number + "===");
        for (Transaction tx : txs) {
            System.out.println(
                    tx.occurredAt() + " | " +
                    tx.type() + " | " +
                    "atm=" + tx.amount() + " | " +
                    "before=" + tx.balanceBefore() + " | " +
                    "after=" + tx.balanceAfter() + " | " +
                    tx.description()
            );
        }
    }

    private void printMenu(){
        System.out.println("""
                1) Crear cuenta
                2) Depositar
                3) Retirar
                4) Transferir
                5) Consultar saldo
                6) Ver historial
                0) Salir
                """);
    }

//    Lectura (validaciones ligeras)

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readAccountNumber(String prompt){
        while (true){
            String input = readLine(prompt);
            if (!input.isBlank()) return input;
            System.out.println("Numero de cuenta requerido");
        }
    }

    private String readOwnerName(String prompt) {
        while (true){
            String input = readLine(prompt);
            if (!input.isBlank()) return input;
            System.out.println("El nombre no puede ir vacio");
        }
    }

    // Depositos/retiros/transferencias (debe ser mayor a 0)

    private BigDecimal readMoneyPositive(String prompt){
        while (true){
            String input = readLine(prompt);

            try {
                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(BigDecimal.ZERO) <= 0){
                    System.out.println("El mmonto debe ser mayor a 0.");
                    continue;
                }

                return value.setScale(2, RoundingMode.HALF_UP);

            } catch (NumberFormatException e){
                System.out.println("Formato invalido. Ejemplo valido: 150.00");
            }
        }
    }

    private BigDecimal readMoneyAllowZero(String prompt) {
        while (true) {
            String input = readLine(prompt);

            try {
                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("El saldo inicial no puede ser negativo");
                    continue;
                }

                return value.setScale(2, RoundingMode.HALF_UP);

            } catch (NumberFormatException e) {
                System.out.println("Formato invalido. Ejemplo valido: 0 o 150.00");
            }
        }
    }

}
