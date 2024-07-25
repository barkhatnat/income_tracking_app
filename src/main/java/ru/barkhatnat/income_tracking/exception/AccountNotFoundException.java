package ru.barkhatnat.income_tracking.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID accountId) {
        super(String.format("Account with ID %s not found", accountId));
    }
}