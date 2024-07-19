package ru.barkhatnat.income_tracking.utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.barkhatnat.income_tracking.DTO.UserResponseDto;
import ru.barkhatnat.income_tracking.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);
}