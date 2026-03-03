package com.mauricio.bank.api.dto;

import java.math.BigDecimal;

public record MoneyRequest(BigDecimal amount) {
}
