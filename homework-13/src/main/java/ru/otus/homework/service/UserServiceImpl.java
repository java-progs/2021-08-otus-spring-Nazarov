package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.User;
import ru.otus.homework.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void updateSuccessLoginTime(String username) {
        val optionalUser = findUser(username);

        if (optionalUser.isEmpty()) {
            return;
        }

        val user = optionalUser.get();
        user.setLastSuccessLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(String username) {
        return findUser(username);
    }

    @Override
    public void increaseAttemptsLogin(String username) {
        val optionalUser = findUser(username);

        if (optionalUser.isEmpty()) {
            return;
        }

        val user = optionalUser.get();
        val attempt = user.getLoginAttempts();
        if (attempt == 0) {
            user.setFirstAttempt(LocalDateTime.now());
        }
        user.setLoginAttempts(attempt + 1);
        userRepository.save(user);
    }

    @Override
    public void resetAttemptsLogin(String username) {
        val optionalUser = findUser(username);

        if (optionalUser.isEmpty()) {
            return;
        }

        val user = optionalUser.get();
        user.setLoginAttempts(0);
        user.setFirstAttempt(null);
        userRepository.save(user);
    }

    private Optional<User> findUser(String userName) {
        User user;
        val userList = userRepository.findAllByName(userName);

        if (userList == null || userList.isEmpty()) {
            user = null;
        }

        user = userList.get(0);

        return Optional.ofNullable(user);
    }
}
