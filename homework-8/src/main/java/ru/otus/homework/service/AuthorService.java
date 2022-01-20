package ru.otus.homework.service;

import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface AuthorService {

    long getCountAuthors();

    List<Author> getAllAuthors();

    List<Author> getAllById(List<String> idList);

    Author getAuthorById(String id) throws RecordNotFoundException;

    Author saveAuthor(Author author);

    boolean updateAuthor(Author author);

    void deleteAuthorById(String id);
}
