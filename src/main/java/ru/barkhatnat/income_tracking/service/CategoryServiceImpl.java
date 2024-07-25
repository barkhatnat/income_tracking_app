package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.CategoryNotFoundException;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.exception.UserNotFoundException;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.utils.CategoryMapper;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public List<Category> findAllCategories(UUID userId) {
        List<Category> defaultCategories = categoryRepository.findCategoriesByUserEmpty();
        List<Category> customCategories = categoryRepository.findCategoriesByUserId(userId);
        return Stream.concat(defaultCategories.stream(), customCategories.stream()).toList();
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryDto categoryDto, UUID userId) {
        User user = userService.findUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Category category = categoryRepository.save(new Category(categoryDto.title(), categoryDto.categoryType(), user));
        return categoryMapper.toCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public Optional<Category> findCategory(UUID id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateCategory(UUID id, String title, Boolean categoryType, UUID userId) {
        categoryRepository.findById(id).ifPresentOrElse(category -> {
            checkCategoryOwnership(id, userId);
            category.setTitle(title);
            category.setCategoryType(categoryType);
        }, () -> {
            throw new CategoryNotFoundException(id);
        });
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id, UUID userId) {
        categoryRepository.findById(id).ifPresentOrElse(category -> {
                    checkCategoryOwnership(id, userId);
                    categoryRepository.deleteById(id);
                }, () -> {
                    throw new CategoryNotFoundException(id);
                }
        );
    }

    private void checkCategoryOwnership(UUID categoryId, UUID userId) {
        if (!categoryRepository.findById(categoryId)
                .map(category -> category.getUser() != null && category.getUser().getId().equals(userId))
                .orElse(false)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
