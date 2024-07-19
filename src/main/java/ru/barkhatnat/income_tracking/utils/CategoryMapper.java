package ru.barkhatnat.income_tracking.utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);
    CategoryResponseDto toCategoryResponseDto(Category category);

    Iterable<CategoryDto> toCategoryDtoCollection(Iterable<Category> categories);

    Iterable<CategoryResponseDto> toCategoryResponseDtoCollection(Iterable<Category> categories);
}
