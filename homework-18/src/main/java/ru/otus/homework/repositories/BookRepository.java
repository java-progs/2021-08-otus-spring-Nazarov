package ru.otus.homework.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.homework.domain.Book;

public interface BookRepository extends MongoRepository<Book, String>, BookRepositoryCustom {

}
