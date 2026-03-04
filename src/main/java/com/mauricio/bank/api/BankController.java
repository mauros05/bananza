package com.mauricio.bank.api;

import com.mauricio.bank.api.dto.*;
import com.mauricio.bank.service.BankService;
import org.springframework.web.bind.annotation.*;

import com.mauricio.bank.api.dto.AccountResponse;
import com.mauricio.bank.api.dto.TransactionResponse;
import com.mauricio.bank.api.dto.TransferResponse;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/accounts")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest req) {
        var account = bankService.createAccount(req.accountNumber(), req.ownerName(), req.initialBalance());
        return toAccountResponse(account);
    }

    @GetMapping("/accounts/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        var account = bankService.getAccount(accountNumber);
        return toAccountResponse(account);
    }

    @PostMapping("/accounts/{accountNumber}/deposit")
    public AccountResponse deposit(@PathVariable String accountNumber, @RequestBody MoneyRequest req) {
        var account = bankService.deposit(accountNumber, req.amount());
        return toAccountResponse(account);
    }

    @PostMapping("/accounts/{accountNumber}/withdraw")
    public AccountResponse withdraw(@PathVariable String accountNumber, @RequestBody MoneyRequest req) {
        var account = bankService.withdraw(accountNumber, req.amount());
        return toAccountResponse(account);
    }

    @PostMapping("/transfers")
    public TransferResponse transfer(@RequestBody TransferRequest req){
        bankService.transfer(req.fromAccountNumber(), req.toAccountNumber(), req.amount());
        return new TransferResponse("Ok");
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public List<TransactionResponse> transactions(@PathVariable String accountNumber){
        return bankService.getTransactions(accountNumber).stream().map(this::toTransactionResponse).toList();
    }

    private AccountResponse toAccountResponse(com.mauricio.bank.persistence.AccountEntity a){
        return new AccountResponse(a.getAccountNumber(), a.getOwnerName(), a.getBalance());
    }

    private TransactionResponse toTransactionResponse(com.mauricio.bank.persistence.TransactionEntity t){
        return new TransactionResponse(
                t.getId(),
                t.getAccountNumber(),
                t.getType(),
                t.getAmount(),
                t.getBalanceBefore(),
                t.getBalanceAfter(),
                t.getDescription(),
                t.getOccurredAt()
        );
    }





}
