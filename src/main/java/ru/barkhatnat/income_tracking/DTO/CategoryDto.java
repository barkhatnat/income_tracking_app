package ru.barkhatnat.income_tracking.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDto(@NotNull
                          @Size(min = 1, max = 32)
                          String title,
                          @NotNull
                          Boolean categoryType) {
}
