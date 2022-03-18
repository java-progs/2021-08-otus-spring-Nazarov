package ru.otus.homework.service;

import ru.otus.homework.domain.User;

import java.util.Optional;

public interface UserService {

    void updateSuccessLoginTime(String username);

    void resetAttemptsLogin(String username);

    Optional<User> getUser(String username);

    void increaseAttemptsLogin(String username);
}
