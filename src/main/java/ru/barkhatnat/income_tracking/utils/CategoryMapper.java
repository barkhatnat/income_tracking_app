package ru.barkhatnat.income_tracking.utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryResponseDto toCategoryResponseDto(Category category);

    Iterable<CategoryResponseDto> toCategoryResponseDtoCollection(Iterable<Category> categories);
}
