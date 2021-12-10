package ru.otus.homework.service;

import ru.otus.homework.domain.Book;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface BookService {

    int getCountBooks();

    List<Book> getAllBooks();

    Book getBookById(long id) throws RecordNotFoundException;

    List<Book> getBooksByAuthor(long authorId);

    List<Book> getBooksByGenre(long genreId);

    boolean addBook(Book book);

    boolean addBook(String name, String isbn, Long[] authorsIds, Long[] genresIds);

    boolean updateBook(Book book);

    boolean updateBook(long id, String name, String isbn, Long[] authorsIds, Long[] genresIds);

    boolean deleteBookById(long id);
}
