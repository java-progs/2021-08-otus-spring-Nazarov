package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.homework.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        val findingUser = repository.findByName(userName);
        if (findingUser == null) {
            throw new UsernameNotFoundException("Not found user " + userName);
        }

        UserDetails user = User.builder()
                .username(findingUser.getName())
                .password(findingUser.getPassword())
                .accountLocked(!findingUser.getAccountNonLocked())
                .roles("USER")
                .build();

        return user;
    }
}
