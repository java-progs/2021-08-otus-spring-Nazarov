package ru.otus.homework.service;

import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface CommentService {

    long getCountComments();

    List<Comment> getAllComments();

    Comment getCommentById(long id) throws RecordNotFoundException;

    List<Comment> getBookComments(long bookId);

    boolean saveComment(Comment comment);

    boolean saveComment(String author, String text, long bookId);

    boolean updateComment(Comment comment);

    boolean updateComment(long id, String text);

    void deleteCommentById(long id);

}
