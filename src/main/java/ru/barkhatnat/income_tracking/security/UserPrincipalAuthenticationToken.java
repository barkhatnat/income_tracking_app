package ru.barkhatnat.income_tracking.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import ru.barkhatnat.income_tracking.entity.security.UserPrincipal;


public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {

    private final UserPrincipal userPrincipal;

    public UserPrincipalAuthenticationToken(UserPrincipal userPrincipal) {
        super(userPrincipal.getAuthorities());
        this.userPrincipal = userPrincipal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return userPrincipal;
    }

}
