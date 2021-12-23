package ru.otus.homework.service;

import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface AuthorService {

    long getCountAuthors();

    List<Author> getAllAuthors();

    Author getAuthorById(long id) throws RecordNotFoundException;

    boolean saveAuthor(Author author);

    boolean updateAuthor(Author author);

    boolean deleteAuthorById(long id);
}
