package ru.barkhatnat.income_tracking.api.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.barkhatnat.income_tracking.DTO.UserCreateDto;
import ru.barkhatnat.income_tracking.DTO.UserResponseDto;
import ru.barkhatnat.income_tracking.DTO.UserUpdateDto;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.UserAlreadyExistsException;
import ru.barkhatnat.income_tracking.repositories.UserRepository;
import ru.barkhatnat.income_tracking.service.UserServiceImpl;
import ru.barkhatnat.income_tracking.utils.UserMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void UserService_CreateUser_ReturnDto() throws UserAlreadyExistsException {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        UserCreateDto userCreateDto = new UserCreateDto("username", "password", "email@email.com");
        UserResponseDto userResponseDto = new UserResponseDto("username", "email@email.com");
        when(userRepository.findByEmail(userCreateDto.email())).thenReturn(Optional.empty());
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(userCreateDto.password())).thenReturn("encodedPassword");
        when(userMapper.toUserResponse(user)).thenReturn(userResponseDto);
        UserResponseDto userResponseDtoTest = userService.createUser(userCreateDto);
        Assertions.assertThat(userResponseDtoTest).isNotNull();
        Assertions.assertThat(userResponseDtoTest.username()).isEqualTo(userCreateDto.username());
        Assertions.assertThat(userResponseDtoTest.email()).isEqualTo(userCreateDto.email());
    }

    @Test
    public void UserService_CreateUserAlreadyExists_ReturnException() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        UserCreateDto userCreateDto = new UserCreateDto("username", "password", "email@email.com");
        when(userRepository.findByEmail(userCreateDto.email())).thenReturn(Optional.of(user));
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(userCreateDto));
        Assertions.assertThat(exception.getMessage()).isEqualTo("User with username username already exists");
    }

    @Test
    public void UserService_FindUserById_ReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(user));
        Optional<User> result = userService.findUser(userId);
        Assertions.assertThat(Optional.of(user)).isEqualTo(result);
    }

    @Test
    public void UserService_FindUserById_ReturnEmpty() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Optional<User> result = userService.findUser(userId);
        Assertions.assertThat(result).isEmpty();
        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    public void UserService_UpdateUser_ReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        UserUpdateDto userUpdateDto = new UserUpdateDto(userId, "new_username", "new_password", "email@email.com");
        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userUpdateDto.password())).thenReturn("encodedPassword"); // Проверка вызова encode()
        userService.updateUser(userUpdateDto);
        Assertions.assertThat(userRepository.findById(userId).get().getUsername()).isEqualTo(userUpdateDto.username());
        Assertions.assertThat(userRepository.findById(userId).get().getPassword()).isEqualTo("encodedPassword");
        Assertions.assertThat(userRepository.findById(userId).get().getEmail()).isEqualTo(userUpdateDto.email());

        Mockito.verify(passwordEncoder).encode(userUpdateDto.password());
    }

    @Test
    public void UserService_UpdateUser_ReturnException() {
        UUID userId = UUID.randomUUID();
        UserUpdateDto userUpdateDto = new UserUpdateDto(userId, "new_username", "new_password", "email@email.com");
        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userService.updateUser(userUpdateDto));
        Mockito.verify(userRepository).findById(userId);
    }
}
