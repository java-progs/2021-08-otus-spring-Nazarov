package ru.otus.homework.dao;

import ru.otus.homework.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {

    long count();

    Optional<Author> getById(long id);

    List<Author> getAll();

    Author save(Author author);

    int deleteById(long id);
}