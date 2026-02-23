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
            String option =

        }


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
