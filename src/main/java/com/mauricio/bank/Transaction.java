package com.mauricio.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
                          UUID id,
                          String accountNumber,
                          TransactionType type,
                          BigDecimal amount,
                          BigDecimal balanceBefore,
                          BigDecimal balanceAfter,
                          String description,
                          LocalDateTime occurredAt
) {}
