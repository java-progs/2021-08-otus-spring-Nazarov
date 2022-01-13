package ru.otus.homework.service;

import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface BookService {

    long getCountBooks();

    List<Book> getAllBooks();

    Book getBookById(String id) throws RecordNotFoundException;

    Book saveBook(Book book);

    Book saveBook(String name, String isbn, List<String> authorsIds, List<String> genresIds);

    boolean updateBook(Book book);

    boolean updateBook(String id, String name, String isbn, List<String> authorsIds, List<String> genresIds);

    void deleteBookById(String id);

    List<Comment> getAllBookComments(String bookId);

    Comment getComment(String bookId, String commentId);

    boolean addComment(String bookId, Comment comment);

    boolean updateComment(String bookId, String commentId, String text);

    boolean deleteComment(String bookId, String commentId);

}
