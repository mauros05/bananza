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
import java.util.Objects;


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

    






}
