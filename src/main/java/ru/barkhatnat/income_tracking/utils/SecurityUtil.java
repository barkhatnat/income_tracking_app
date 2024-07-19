package ru.barkhatnat.income_tracking.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.barkhatnat.income_tracking.entity.security.UserPrincipal;

@Component
public class SecurityUtil {
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UserPrincipal getCurrentUserDetails() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }
}
