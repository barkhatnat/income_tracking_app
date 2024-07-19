package ru.barkhatnat.income_tracking.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDto (UUID id, String title, BigDecimal balance) {
}
