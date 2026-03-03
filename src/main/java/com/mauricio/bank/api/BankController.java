package com.mauricio.bank.api;

import com.mauricio.bank.Bank;
import com.mauricio.bank.Transaction;
import com.mauricio.bank.api.dto.CreateAccountRequest;
import com.mauricio.bank.api.dto.MoneyRequest;
import com.mauricio.bank.api.dto.TransferRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BankController {
    private final Bank bank;

    public BankController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping("/accounts")
    public Map<String, Object> createAccount(@RequestBody CreateAccountRequest req) {
        var account = bank.createAccount(req.accountNumber(), req.ownerName(), req.initialBalance());
        return Map.of(
                "accountNumber", account.getAccountNumber(),
                "ownerName", account.getOwnerName(),
                "balance", account.getBalance()
        );
    }

    @GetMapping("/accounts/{accountNumber}")
    public Map<String, Object> getAccount(@PathVariable String accountNumber) {
        var account = bank.getAccount(accountNumber);
        return Map.of(
                "accountNumber", account.getAccountNumber(),
                "ownerName", account.getOwnerName(),
                "balance", account.getBalance()
        );
    }

    @PostMapping("/accounts/{accountNumber}/deposit")
    public Map<String, BigDecimal> deposit(@PathVariable String accountNumber, @RequestBody MoneyRequest req) {
        bank.deposit(accountNumber, req.amount());
        return Map.of("balance", bank.getAccount(accountNumber).getBalance());
    }

    @PostMapping("/accounts/{accountNumber}/withdraw")
    public Map<String, BigDecimal> withdraw(@PathVariable String accountNumber, @RequestBody MoneyRequest req) {
        bank.withdraw(accountNumber, req.amount());
        return Map.of("balance", bank.getAccount(accountNumber).getBalance());
    }

    @PostMapping("/transfers")
    public Map<String, Object> transfer(@RequestBody TransferRequest req){
        bank.transfer(req.fromAccountNumber(), req.toAccountNumber(), req.amount());
        return Map.of("status", "OK");
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public List<Transaction> transactions(@PathVariable String accountNumber){
        return bank.getTransactions(accountNumber);
    }






}
