package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.UserCreateDto;
import ru.barkhatnat.income_tracking.DTO.UserResponseDto;
import ru.barkhatnat.income_tracking.DTO.UserUpdateDto;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.UserAlreadyExistsException;
import ru.barkhatnat.income_tracking.repositories.UserRepository;
import ru.barkhatnat.income_tracking.utils.UserMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto userCreateDto) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + userCreateDto.username() + " already exists");
        }
        String encodedPassword = passwordEncoder.encode(userCreateDto.password());
        User user = userRepository.save(new User(userCreateDto.username(), encodedPassword, userCreateDto.email(), getCreationDate(), getRole()));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public Optional<User> findUser(UUID id) {
        return this.userRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateDto userUpdateDto) {
        String encodedPassword = passwordEncoder.encode(userUpdateDto.password());
        this.userRepository.findById(userUpdateDto.id()).ifPresentOrElse(user -> {
                    user.setUsername(userUpdateDto.username());
                    user.setPassword(encodedPassword);
                    user.setEmail(userUpdateDto.email());
                }, () -> {
                    throw new NoSuchElementException();
                }
        );
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    private Timestamp getCreationDate() {
        return Timestamp.from(Instant.now());
    }

    private String getRole() {
        return "USER";
    }
}
