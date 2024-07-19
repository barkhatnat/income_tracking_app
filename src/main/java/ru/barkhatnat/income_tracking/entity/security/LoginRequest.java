package ru.barkhatnat.income_tracking.entity.security;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginRequest {
    private String email;
    private String password;
}
