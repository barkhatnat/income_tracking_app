package ru.barkhatnat.income_tracking.DTO;

import java.util.UUID;

public record UserResponseDto(UUID id,
                              String username,
                              String email) {
}