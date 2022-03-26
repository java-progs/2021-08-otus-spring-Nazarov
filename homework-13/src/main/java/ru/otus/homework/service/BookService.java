package ru.otus.homework.service;

import ru.otus.homework.domain.Book;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface BookService {

    long getCountBooks();

    List<Book> getAllBooks();

    Book getBookById(long id) throws RecordNotFoundException;

    List<Book> getAllByAuthor(long authorId);

    List<Book> getAllByGenre(long genreId);

    boolean saveBook(Book book);

    boolean saveBook(String name, String isbn, Long[] authorsIds, Long[] genresIds);

    boolean updateBook(Book book);

    boolean updateBook(long id, String name, String isbn, Long[] authorsIds, Long[] genresIds);

    void deleteBookById(long id);
}
