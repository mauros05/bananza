package com.mauricio.bank.api.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        String ownerName,
        BigDecimal balance
) {}