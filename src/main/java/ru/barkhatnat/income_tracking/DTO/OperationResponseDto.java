package ru.barkhatnat.income_tracking.DTO;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record OperationResponseDto(UUID id, BigDecimal amount, Timestamp
        datePurchase, UUID categoryId, String note) {
}
