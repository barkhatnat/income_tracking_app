package ru.barkhatnat.income_tracking.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserUpdateDto(
        @NotNull()
        UUID id,
        @NotNull()
        @Size(min = 1, max = 64)
        String username,
        @NotNull()
        @Size(min = 1, max = 256)
        String password,
        @NotNull()
        @Size(min = 1, max = 128)
        String email) {
}
