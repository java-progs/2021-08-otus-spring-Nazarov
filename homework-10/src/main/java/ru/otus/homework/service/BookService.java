package ru.otus.homework.service;

import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.ObjectNotFoundException;

import java.util.List;

public interface BookService {

    long getCountBooks();

    List<Book> getAllBooks();

    List<Book> getByAuthor(String id);

    List<Book> getByGenre(String id);

    Book getBookById(String id) throws ObjectNotFoundException;

    Book saveBook(Book book);

    boolean updateBook(Book book);

    void deleteBookById(String id);

    List<Comment> getAllBookComments(String bookId);

    Comment getComment(String bookId, String commentId) throws ObjectNotFoundException;

    boolean addComment(String bookId, Comment comment);

    boolean updateComment(String bookId, Comment comment);

    boolean deleteComment(String bookId, String commentId);

    boolean existById(String id);
}
