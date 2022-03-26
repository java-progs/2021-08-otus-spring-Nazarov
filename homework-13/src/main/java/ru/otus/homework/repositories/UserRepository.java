package ru.otus.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.homework.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByName(String name);

}
