package ru.otus.homework.dao;

import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;

import java.util.List;

public interface AuthorDao {

    int count();

    Author getById(long id);

    List<Author> getAll();

    List<Author> getActiveAuthors();

    List<Author> getBookAuthors(Book book);

    Author insert(Author author);

    Author update(Author author);

    int deleteById(long id);
}
