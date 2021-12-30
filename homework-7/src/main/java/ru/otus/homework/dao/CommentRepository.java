package ru.otus.homework.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.homework.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBookId(long id);

}