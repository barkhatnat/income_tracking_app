package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.entity.security.UserPrincipal;
import ru.barkhatnat.income_tracking.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);
        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                logger.warn("User not found: {}", email);
                throw new UsernameNotFoundException("User not found");
            }
            logger.debug("User found: {}", email);
            return UserPrincipal.builder()
                    .userId(user.get().getId())
                    .email(user.get().getEmail())
                    .password(user.get().getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority(user.get().getRole()))).build();
        } catch (Exception e) {
            logger.error("An internal error occurred while trying to authenticate the user: {}", email, e);
            throw new InternalAuthenticationServiceException("An internal error occurred while trying to authenticate the user.", e);
        }
    }
}
