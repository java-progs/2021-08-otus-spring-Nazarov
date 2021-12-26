package ru.otus.homework.dao;

import lombok.val;
import org.springframework.stereotype.Repository;
import ru.otus.homework.domain.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryJpa implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    public CommentRepositoryJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public long count() {
        val query = em.createQuery("select count(c) from Comment c", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<Comment> getById(long id) {
        val query = em.createQuery(
                "select c from Comment c join fetch c.book where c.id = :id",
                Comment.class);
        query.setParameter("id", id);
        Comment result;

        try {
            result = query.getSingleResult();
        } catch (Exception e) {
            result = null;
        }

        return Optional.ofNullable(result);
    }

    @Override
    public List<Comment> getAll() {
        val query = em.createQuery("select c from Comment c join fetch c.book", Comment.class);
        return query.getResultList();
    }

    @Override
    public List<Comment> getBookComments(long bookId) {
        val query = em.createQuery("select c from Comment c where c.book.id = :book_id", Comment.class);
        query.setParameter("book_id", bookId);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        } else {
            return em.merge(comment);
        }
    }

    @Override
    public int deleteById(long id) {
        val query = em.createQuery("delete from Comment where id = :id");
        query.setParameter("id", id);
        return query.executeUpdate();
    }
}
