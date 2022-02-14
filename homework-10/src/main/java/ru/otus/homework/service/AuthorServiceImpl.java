package ru.otus.homework.service;

import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository repository;

    public AuthorServiceImpl(AuthorRepository repository) {
        this.repository = repository;
    }

    @Override
    public long getCountAuthors() {
        return repository.count();
    }

    @Override
    public List<Author> getAllAuthors() {
        return repository.findAll();
    }

    @Override
    public List<Author> getAllById(List<String> idList) {
        val authorsList = new ArrayList<Author>();
        repository.findAllById(idList).forEach(a -> authorsList.add(a));

        return authorsList;
    }

    @Override
    public Author getAuthorById(String id) throws ObjectNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Not found author with id = %s", id)));
    }

    @Override
    public Author saveAuthor(Author author) {
        Author savedAuthor;

        try {
            savedAuthor = repository.save(author);
        } catch (Exception e) {
            return null;
        }

        return savedAuthor;
    }

    @Override
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
    public void deleteAuthorById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existById(String id) {
        return repository.existsById(id);
    }
}
