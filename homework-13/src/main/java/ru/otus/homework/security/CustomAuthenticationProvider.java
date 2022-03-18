package ru.otus.homework.security;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.otus.homework.service.UserService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final UserService userService;

    private final int MAX_ATTEMPTS = 3;
    private final long BLOCK_TIME_IN_SECONDS = 60 * 3;


    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            val authenticationResult = super.authenticate(authentication);
            val username = authentication.getName();

            val optionalUser = userService.getUser(username);
            val user = optionalUser.get();

            if (user.getLoginAttempts() >= MAX_ATTEMPTS
                    && !isExpired(user.getFirstAttempt(), BLOCK_TIME_IN_SECONDS)) {
                throw new LockedException("Account locked until "
                        + user.getFirstAttempt().plusSeconds(BLOCK_TIME_IN_SECONDS));
            }

            userService.updateSuccessLoginTime(username);
            userService.resetAttemptsLogin(username);

            return authenticationResult;
        } catch (BadCredentialsException e) {
            userService.increaseAttemptsLogin(authentication.getName());
            throw e;
        }
    }

    private boolean isExpired(LocalDateTime checkTime, long shift) {
        val currentTime = LocalDateTime.now();
        val expiredTime = checkTime.plusSeconds(shift);
        return currentTime.isAfter(expiredTime);
    }
}
