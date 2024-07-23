package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.barkhatnat.income_tracking.DTO.UserResponseDto;
import ru.barkhatnat.income_tracking.DTO.UserUpdateDto;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.service.UserService;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;
import ru.barkhatnat.income_tracking.utils.UserMapper;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class UserRestController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser() {
        UUID id = securityUtil.getCurrentUserDetails().getUserId();
        Optional<User> user = userService.findUser(id);
        return user.map(value -> ResponseEntity.ok(userMapper.toUserResponse(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.userService.updateUser(userUpdateDto);
            return ResponseEntity.noContent().build();
        }
    }
}
