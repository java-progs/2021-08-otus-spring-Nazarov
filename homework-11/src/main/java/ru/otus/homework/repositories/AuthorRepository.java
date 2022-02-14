package ru.otus.homework.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.homework.domain.Author;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

}