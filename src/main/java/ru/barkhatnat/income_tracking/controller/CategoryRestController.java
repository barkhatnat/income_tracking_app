package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.exception.CategoryNotFoundException;
import ru.barkhatnat.income_tracking.service.CategoryService;
import ru.barkhatnat.income_tracking.utils.CategoryMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/categories/{categoryId:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
public class CategoryRestController {
    private final CategoryService categoryService;
    private final SecurityUtil securityUtil;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable("categoryId") UUID categoryId) {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        Optional<Category> Category = categoryService.findCategory(categoryId, currentUserId);
        return Category.map(value -> ResponseEntity.ok(categoryMapper.toCategoryResponseDto(value))).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @PatchMapping
    public ResponseEntity<?> updateCategory(@PathVariable("categoryId") UUID categoryId, @Valid @RequestBody CategoryDto categoryDto,
                                            BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
            categoryService.updateCategory(categoryId, categoryDto.title(), categoryDto.categoryType(), currentUserId);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") UUID categoryId) {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        categoryService.deleteCategory(categoryId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
