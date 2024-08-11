package ru.barkhatnat.income_tracking.api.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.barkhatnat.income_tracking.entity.security.UserPrincipal;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityUtilTest {

    @InjectMocks
    private SecurityUtil securityUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @Test
    public void securityUtil_GetCurrentUserDetails_ReturnAuthenticatedUserDetails() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal actualUserPrincipal = securityUtil.getCurrentUserDetails();
        assertEquals(userPrincipal, actualUserPrincipal);
    }

    @Test
    public void securityUtil_GetCurrentUserDetails_ReturnException() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThrows(AuthenticationException.class, () -> securityUtil.getCurrentUserDetails());
    }
}