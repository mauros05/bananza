package com.mauricio.bank.api.dto;

import java.math.BigDecimal;

public record TransferRequest(
            String fromAccountNumber,
            String toAccountNumber,
            BigDecimal amount
) {}
