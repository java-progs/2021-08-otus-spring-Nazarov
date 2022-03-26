package ru.otus.homework.service;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface CommentService {

    long getCountComments();

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Comment> getAllComments();

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    Comment getCommentById(long id) throws RecordNotFoundException;

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Comment> getBookComments(long bookId);

    //права на запись, либо автор, либо новый коммент (id = 0)
    @PreAuthorize("hasPermission(#comment, 'WRITE') " +
            "or (#comment.author == authentication.name) " +
            "or (#comment.id == 0)")
    boolean saveComment(Comment comment);

    //права на запись, либо автор
    @PreAuthorize("hasPermission(#comment, 'WRITE') " +
            "or (#comment.author == authentication.name)")
    boolean updateComment(Comment comment);

    //права на удаление, либо автор
    @PreAuthorize("hasPermission(#comment, 'DELETE') " +
            "or (#comment.author == authentication.name)")
    void deleteComment(Comment comment);

}
