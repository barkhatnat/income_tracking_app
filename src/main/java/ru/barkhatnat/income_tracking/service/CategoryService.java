package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryService {
    Iterable<Category> findAllCategories();

    CategoryResponseDto createCategory(CategoryDto categoryDto);

    Optional<Category> findCategory(UUID id);

    void updateCategory(UUID id, String title, Boolean categoryType);

    void deleteCategory(UUID id);
}
