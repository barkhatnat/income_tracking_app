package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface OperationService {

    Iterable<Operation> findAllOperations();
    Iterable<Operation> findAllAccountOperations(UUID accountId);

    OperationResponseDto createOperation(OperationDto operationDto, UUID currentAccountId);

    Optional<Operation> findOperation(UUID id);

    void updateOperation(UUID id, BigDecimal amount, Timestamp datePurchase, UUID categoryId, String note, UUID currentAccountId);

    void deleteOperation(UUID id, UUID currentAccountId);
}
