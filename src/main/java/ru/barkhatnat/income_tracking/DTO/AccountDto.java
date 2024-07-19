package ru.barkhatnat.income_tracking.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountDto(
        @Size(min = 1, max = 16, message = "{accounts.create.errors.invalid_title_size}")
        String title,
        @NotNull(message = "{accounts.create.errors.null_balance}")
        @DecimalMin(value = "-9999999999.99", message = "{accounts.create.errors.invalid_balance_size}")
        @DecimalMax(value = "9999999999.99", message = "{accounts.create.errors.invalid_balance_size}")
        BigDecimal balance) {
}
