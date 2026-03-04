package com.mauricio.bank.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TransferRequest(
            @NotBlank
            @Pattern(regexp = "\\d{3,20}", message = "fromAccountNumber must be only digits, length 3-20")
            String fromAccountNumber,

            @NotBlank
            @Pattern(regexp = "\\d{3,20}", message = "toAccountNumber must be only digits, length 3-20")
            String toAccountNumber,

            @NotNull(message = "amount is required")
            @DecimalMin(value = "0.01", inclusive = true, message = "amount must be > 0")
            @Digits(integer = 18, fraction = 2, message = "amount must have 2 decimals")
            BigDecimal amount
) {}
