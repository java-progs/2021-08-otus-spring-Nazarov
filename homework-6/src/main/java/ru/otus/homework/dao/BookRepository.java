package ru.otus.homework.dao;

import ru.otus.homework.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    long count();

    Optional<Book> getById(long id);

    List<Book> getAll();

    Book save(Book book);

    int deleteById(long id);

}
