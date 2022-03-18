package ru.otus.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.homework.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Comment> findAllByBookId(long id);

    //права на запись, либо автор, либо новый коммент (id = 0)
    @PreAuthorize("hasPermission(#comment, 'WRITE') " +
            "or (#comment.author == authentication.name) " +
            "or (#comment.id == 0)")
    Comment save(@Param("comment") Comment comment);

    //права на удаление, либо автор
    @PreAuthorize("hasPermission(#comment, 'DELETE') " +
            "or (#comment.author == authentication.name)")
    void delete(@Param("comment") Comment comment);

}