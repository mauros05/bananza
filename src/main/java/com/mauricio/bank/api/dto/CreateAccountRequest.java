package com.mauricio.bank.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateAccountRequest(
                @NotBlank
                @Pattern(regexp = "\\d{3,20}", message = "accountNumber must be only digits, length 3-20")
                String accountNumber,

                @NotBlank(message = "ownerName is required")
                String ownerName,

                @NotNull(message = "initialBalance is required")
                @DecimalMin(value = "0.00", inclusive = true, message = "initialBalance must be >= 0.00")
                @Digits(integer = 18, fraction = 2, message = "initialBalance must have up to 2 decimals")
                BigDecimal initialBalance
) {}
