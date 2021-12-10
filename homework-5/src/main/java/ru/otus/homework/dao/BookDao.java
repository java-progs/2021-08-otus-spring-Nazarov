package ru.otus.homework.dao;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Book;

import java.util.List;

public interface BookDao {
    int count();

    Book getById(long id);

    List<Book> getAll();

    List<Book> getBooksByAuthor(long id);

    List<Book> getBooksByGenre(long id);

    Book insert(Book book);

    @Transactional
    Book update(Book book);

    @Transactional
    int deleteById(long id);
}
