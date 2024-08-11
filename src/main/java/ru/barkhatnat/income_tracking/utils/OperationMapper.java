package ru.barkhatnat.income_tracking.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Operation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface OperationMapper {

    @Mapping(target = "categoryId", source = "category.id")
    OperationResponseDto toOperationResponseDto(Operation operation);

    Iterable<OperationResponseDto> toOperationResponseDtoCollection(Iterable<Operation> operations);
}
