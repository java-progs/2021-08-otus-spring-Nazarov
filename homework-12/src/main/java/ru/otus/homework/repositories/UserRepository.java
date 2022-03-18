package ru.otus.homework.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.homework.domain.User;

public interface UserRepository extends MongoRepository<User, String> {

    User findByName(String name);

}
