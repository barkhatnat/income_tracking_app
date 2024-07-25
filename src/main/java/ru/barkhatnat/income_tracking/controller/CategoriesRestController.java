package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.service.CategoryService;
import ru.barkhatnat.income_tracking.utils.CategoryMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.net.URI;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/categories")
public class CategoriesRestController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<Iterable<CategoryResponseDto>> getCategoryList() {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        Iterable<Category> categories = categoryService.findAllCategories(currentUserId);
        Iterable<CategoryResponseDto> categoryResponseDto = categoryMapper.toCategoryResponseDtoCollection(categories);
        return ResponseEntity.ok(categoryResponseDto);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto,
                                            BindingResult bindingResult,
                                            UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
            CategoryResponseDto categoryResponseDto = categoryService.createCategory(categoryDto, currentUserId);
            return ResponseEntity.created(URI.create(uriComponentsBuilder
                            .replacePath("/categories")
                            .build().toUriString()))
                    .body(categoryResponseDto);
        }
    }
}
