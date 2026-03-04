package com.mauricio.bank.api;

import com.mauricio.bank.api.dto.*;
import com.mauricio.bank.persistence.AccountEntity;
import com.mauricio.bank.persistence.TransactionEntity;
import com.mauricio.bank.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/accounts")
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest req) {
        var account = bankService.createAccount(req.accountNumber(), req.ownerName(), req.initialBalance());
        return toAccountResponse(account);
    }

    @GetMapping("/accounts/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        var account = bankService.getAccount(accountNumber);
        return toAccountResponse(account);
    }

    @PostMapping("/accounts/{accountNumber}/deposit")
    public AccountResponse deposit(@Valid @PathVariable String accountNumber, @RequestBody MoneyRequest req) {
        var account = bankService.deposit(accountNumber, req.amount());
        return toAccountResponse(account);
    }

    @PostMapping("/accounts/{accountNumber}/withdraw")
    public AccountResponse withdraw(@Valid @PathVariable String accountNumber, @RequestBody MoneyRequest req) {
        var account = bankService.withdraw(accountNumber, req.amount());
        return toAccountResponse(account);
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@Valid @RequestBody TransferRequest req){
        bankService.transfer(req.fromAccountNumber(), req.toAccountNumber(), req.amount());
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public Page<TransactionResponse> transactions(@PathVariable String accountNumber, @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return bankService.getTransactions(accountNumber, pageable).map(this::toTransactionResponse);
    }

    // ---- Mappers ----

    private AccountResponse toAccountResponse(AccountEntity a){
        return new AccountResponse(a.getAccountNumber(), a.getOwnerName(), a.getBalance());
    }

    private TransactionResponse toTransactionResponse(TransactionEntity t){
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
