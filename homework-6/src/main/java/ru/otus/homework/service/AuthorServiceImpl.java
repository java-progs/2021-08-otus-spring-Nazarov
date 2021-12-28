package ru.otus.homework.service;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.AuthorRepository;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository repository;

    public AuthorServiceImpl(AuthorRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public long getCountAuthors() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return repository.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Author getAuthorById(long id) throws RecordNotFoundException {
        return repository.getById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found author with id = %d", id)));

    }

    @Override
    @Transactional
    public boolean saveAuthor(Author author) {
        try {
            repository.save(author);
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
            updatedAuthor = repository.save(author);
        } catch (Exception e) {
            return false;
        }

        return author.equals(updatedAuthor);
    }

    @Override
    @Transactional
    public boolean deleteAuthorById(long id) {
        var deletedRows = 0;

        try {
            deletedRows = repository.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return deletedRows == 1;
    }

}
