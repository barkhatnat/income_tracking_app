package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.barkhatnat.income_tracking.DTO.UserCreateDto;
import ru.barkhatnat.income_tracking.DTO.UserResponseDto;
import ru.barkhatnat.income_tracking.entity.security.LoginRequest;
import ru.barkhatnat.income_tracking.entity.security.LoginResponse;
import ru.barkhatnat.income_tracking.entity.security.UserPrincipal;
import ru.barkhatnat.income_tracking.exception.UserAlreadyExistsException;
import ru.barkhatnat.income_tracking.security.JwtIssuer;
import ru.barkhatnat.income_tracking.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<?> createAccount(@Valid @RequestBody UserCreateDto userCreateDto,
                                           BindingResult bindingResult,
                                           UriComponentsBuilder uriComponentsBuilder) throws BindException, UserAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UserResponseDto userResponseDto = userService.createUser(userCreateDto);
            return ResponseEntity.created(URI.create(uriComponentsBuilder
                            .replacePath("/accounts")
                            .build().toUriString()))
                    .body(userResponseDto);
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String token = jwtIssuer.issue(principal.getUserId(), principal.getEmail(), roles);
        return LoginResponse.builder()
                .accessToken(token).build();
    }
}
