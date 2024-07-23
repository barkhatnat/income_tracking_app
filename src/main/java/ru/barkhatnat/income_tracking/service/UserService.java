package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.UserCreateDto;
import ru.barkhatnat.income_tracking.DTO.UserResponseDto;
import ru.barkhatnat.income_tracking.DTO.UserUpdateDto;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.UserAlreadyExistsException;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(UserCreateDto userCreateDto) throws UserAlreadyExistsException;

    Optional<User> findUser(UUID id);

    void updateUser(UserUpdateDto userUpdateDto);

    void deleteUser(UUID id);
}
