package com.mauricio.bank.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MoneyRequest(
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "amount must be > 0")
        @Digits(integer = 18, fraction = 2, message = "amount must have up to 2 decimals")
        BigDecimal amount
) {}
