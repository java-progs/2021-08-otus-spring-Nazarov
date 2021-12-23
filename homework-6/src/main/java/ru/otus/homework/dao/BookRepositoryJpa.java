package ru.otus.homework.dao;

import lombok.val;
import org.springframework.stereotype.Repository;
import ru.otus.homework.domain.Book;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepositoryJpa implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookRepositoryJpa(EntityManager em, AuthorRepository authorDao, GenreRepository genreDao) {
        this.em = em;
        this.authorRepository = authorDao;
        this.genreRepository = genreDao;
    }

    @Override
    public long count() {
        val query = em.createQuery("select count(b) from Book b", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<Book> getById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public List<Book> getAll() {
        val query = em.createQuery("select b from Book b", Book.class);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public int deleteById(long id) {
        val query = em.createQuery("delete from Book b where id = :id");
        query.setParameter("id", id);
        return query.executeUpdate();
    }

}
