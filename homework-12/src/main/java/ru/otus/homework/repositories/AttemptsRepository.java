package ru.otus.homework.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.homework.domain.Attempts;

public interface AttemptsRepository extends MongoRepository<Attempts, String> {

    Attempts findAttemptsByUsername(String username);

}
