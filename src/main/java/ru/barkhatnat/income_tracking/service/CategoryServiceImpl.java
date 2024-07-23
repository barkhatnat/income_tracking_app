package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.UserNotFoundException;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.utils.CategoryMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryMapper categoryMapper;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public List<Category> findAllCategories() {
        List<Category> defaultCategories = categoryRepository.findCategoriesByUserEmpty();
        UUID id = securityUtil.getCurrentUserDetails().getUserId();
        List<Category> customCategories = categoryRepository.findCategoriesByUserId(id);
        return Stream.concat(defaultCategories.stream(), customCategories.stream()).toList();
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryDto categoryDto) {
        UUID id = securityUtil.getCurrentUserDetails().getUserId();
        Optional<User> user = userService.findUser(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        Category category = categoryRepository.save(new Category(categoryDto.title(), categoryDto.categoryType(), user.get()));
        return categoryMapper.toCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public Optional<Category> findCategory(UUID id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateCategory(UUID id, String title, Boolean categoryType) {
        categoryRepository.findById(id).ifPresentOrElse(category -> {
            if (category.getUser() != null && category.getUser().getId().equals(securityUtil.getCurrentUserDetails().getUserId())) {
                category.setTitle(title);
                category.setCategoryType(categoryType);
            } else {
                throw new IllegalArgumentException("You do not have permission to update this category.");
            }
        }, () -> {
            throw new NoSuchElementException();
        });
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.findById(id).ifPresentOrElse(category -> {
                    if (category.getUser() != null && category.getUser().getId().equals(securityUtil.getCurrentUserDetails().getUserId())) {
                        categoryRepository.deleteById(id);
                    } else {
                        throw new IllegalArgumentException("You do not have permission to delete this category.");
                    }
                }, () -> {
                    throw new NoSuchElementException();
                }
        );
    }
}
