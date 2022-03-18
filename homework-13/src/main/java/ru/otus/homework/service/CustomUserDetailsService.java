package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.homework.repositories.UserRepository;

@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        val usersList = repository.findAllByName(userName);
        if (usersList == null || usersList.size() != 1) {
            throw new UsernameNotFoundException("Not found user " + userName);
        }

        val findingUser = usersList.get(0);
        val roles = findingUser.getRoles().stream().map(r -> r.getName()).toArray(String[]::new);

        UserDetails user = User.builder()
                .username(findingUser.getName())
                .password(findingUser.getPassword())
                .accountLocked(!findingUser.getAccountNonLocked())
                .roles(roles)
                .build();

        return user;
    }
}
