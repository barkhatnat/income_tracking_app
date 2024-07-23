package ru.barkhatnat.income_tracking.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super(String.format("User with ID %s not found", userId));
    }
}