package ru.barkhatnat.income_tracking.DTO;

import java.util.UUID;

public record CategoryResponseDto(UUID id, String title, Boolean categoryType) {
}
