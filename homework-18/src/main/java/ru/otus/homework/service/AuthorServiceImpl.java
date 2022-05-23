package ru.otus.homework.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository repository;

    public AuthorServiceImpl(AuthorRepository repository) {
        this.repository = repository;
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public long getCountAuthors() {
        return repository.count();
    }

    @HystrixCommand(commandKey = "readFromDbKey", fallbackMethod = "getEmptyList")
    @Override
    public List<Author> getAllAuthors() {
        return repository.findAll();
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public List<Author> getAllById(List<String> idList) {
        val authorsList = new ArrayList<Author>();
        repository.findAllById(idList).forEach(a -> authorsList.add(a));

        return authorsList;
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public Author getAuthorById(String id) throws RecordNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found author with id = %s", id)));
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public Author saveAuthor(Author author) {
        Author savedAuthor = repository.save(author);

        return savedAuthor;
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public boolean updateAuthor(Author author) {
        Author updatedAuthor = repository.save(author);

        return author.equals(updatedAuthor);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public void deleteAuthorById(String id) {
        repository.deleteById(id);
    }

    private List<Author> getEmptyList() {
        return new ArrayList<>();
    }

}
