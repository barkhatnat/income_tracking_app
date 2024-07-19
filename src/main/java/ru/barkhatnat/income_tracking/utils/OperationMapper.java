package ru.barkhatnat.income_tracking.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Operation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface OperationMapper {

    @Mapping(target = "categoryId", source = "category.id")
    OperationDto toOperationDto(Operation operation);

    @Mapping(target = "categoryId", source = "category.id")
    OperationResponseDto toOperationResponseDto(Operation operation);

    Iterable<OperationDto> toOperationDtoCollection(Iterable<Operation> operations);

    Iterable<OperationResponseDto> toOperationResponseDtoCollection(Iterable<Operation> operations);
}
