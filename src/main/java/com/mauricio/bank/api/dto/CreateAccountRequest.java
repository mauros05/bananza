package com.mauricio.bank.api.dto;

import java.math.BigDecimal;

public record CreateAccountRequest(
                String accountNumber,
                String ownerName,
                BigDecimal initialBalance
) {}
