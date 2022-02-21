package ru.otus.homework.repositories;

import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;

import java.util.List;

public interface BookRepositoryCustom {

    List<Book> findByAuthor(String authorId);

    List<Book> findByGenre(String genreId);

    Comment findComment(String bookId, String commentId);

    Long getCountByAuthor(String authorId);

    Long getCountByGenre(String genreId);

    boolean addComment(String bookId, Comment comment);

    boolean updateComment(String bookId, Comment comment);

    boolean deleteComment(String bookId, String commentId);

}
