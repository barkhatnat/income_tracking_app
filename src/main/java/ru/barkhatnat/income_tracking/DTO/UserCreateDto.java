package ru.barkhatnat.income_tracking.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotNull()
        @Size(min = 1, max = 64)
        String username,
        @NotNull()
        @Size(min = 1, max = 256)
        String password,
        @NotNull()
        @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9-]+.+.[a-z]{2,4}")
        @Size(min = 1, max = 128)
        String email) {
}
