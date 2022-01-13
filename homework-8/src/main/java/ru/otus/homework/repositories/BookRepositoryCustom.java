package ru.otus.homework.repositories;

import ru.otus.homework.domain.Comment;

public interface BookRepositoryCustom {

    Comment findComment(String bookId, String commentId);

    boolean addComment(String bookId, Comment comment);

    boolean updateComment(String bookId, Comment comment);

    boolean deleteComment(String bookId, String commentId);

}
