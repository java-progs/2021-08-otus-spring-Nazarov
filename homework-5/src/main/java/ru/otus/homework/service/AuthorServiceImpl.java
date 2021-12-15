package ru.otus.homework.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.AuthorDao;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao dao;

    public AuthorServiceImpl(AuthorDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCountAuthors() {
        return dao.count();
    }

    @Override
    public List<Author> getAllAuthors() {
        return dao.getAll();
    }

    @Override
    public Author getAuthorById(long id) throws RecordNotFoundException {
        try {
            return dao.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Not found author with id = %d", id));
        }
    }

    @Override
    @Transactional
    public boolean addAuthor(Author author) {
        try {
            dao.insert(author);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateAuthor(Author author) {
        Author updatedAuthor;

        try {
            updatedAuthor = dao.update(author);
        } catch (Exception e) {
            return false;
        }

        return author.equals(updatedAuthor);
    }

    @Override
    @Transactional
    public boolean deleteAuthorById(long id) {
        int deletedRows = 0;

        try {
            deletedRows = dao.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return deletedRows == 1;
    }
}
