package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.service.CategoryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/categories/{categoryId:\\d+}") //TODO change pattern for uuid
public class CategoryRestController {
    private final CategoryService categoryService;

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
            categoryService.updateCategory(categoryId, categoryDto.title(), categoryDto.categoryType());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
