package ru.barkhatnat.income_tracking.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.barkhatnat.income_tracking.entity.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

@Component
public class JwtToPrincipalConverter {
    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(UUID.fromString(jwt.getSubject()))
                .email(jwt.getClaim("e").asString())
                .authorities(extractAuthoritiesFromClaim(jwt))
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt) {
        var claim = jwt.getClaim("a");
        if (claim.isNull() || claim.isMissing()) {
            return List.of();
        }
        return claim.asList(SimpleGrantedAuthority.class);
    }
}
