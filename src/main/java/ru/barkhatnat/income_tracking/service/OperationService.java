package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationService {

    List<Operation> findAllOperationsByAccountId(UUID accountId, UUID userId);

    OperationResponseDto createOperation(OperationDto operationDto, UUID currentAccountId, UUID userId);

    Optional<Operation> findOperation(UUID id);

    void updateOperation(UUID id, BigDecimal amount, Timestamp datePurchase, UUID categoryId, String note, UUID currentAccountId, UUID userId);

    void deleteOperation(UUID id, UUID currentAccountId, UUID userId);
}
