package com.mauricio.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(String accountNumber,
                          TransactionType type,
                          BigDecimal amount,
                          String description,
                          LocalDateTime occurredAt) {}
