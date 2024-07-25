package ru.barkhatnat.income_tracking.exception;

import java.util.UUID;

public class OperationNotFoundException extends RuntimeException {
    public OperationNotFoundException(UUID operationId) {
        super(String.format("Operation with ID %s not found", operationId));
    }
}