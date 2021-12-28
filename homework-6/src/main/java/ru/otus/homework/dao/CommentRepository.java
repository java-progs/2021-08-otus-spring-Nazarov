package ru.otus.homework.dao;

import ru.otus.homework.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    long count();

    Optional<Comment> getById(long id);

    List<Comment> getAll();

    List<Comment> getBookComments(long bookId);

    Comment save(Comment comment);

    int deleteById(long id);

}