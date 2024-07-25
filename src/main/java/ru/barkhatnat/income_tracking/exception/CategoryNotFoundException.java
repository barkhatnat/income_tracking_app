package ru.barkhatnat.income_tracking.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID categoryId) {
        super(String.format("Category with ID %s not found", categoryId));
    }
}
