package ru.otus.homework.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Author;
import ru.otus.homework.repositories.GenreRepository;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository repository;

    public GenreServiceImpl(GenreRepository repository) {
        this.repository = repository;
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public long getCountGenres() {
        return repository.count();
    }

    @HystrixCommand(commandKey = "readFromDbKey", fallbackMethod = "getEmptyList")
    @Override
    public List<Genre> getAllGenres() {
        return repository.findAll();
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public List<Genre> getAllById(List<String> idList) {
        val genresList = new ArrayList<Genre>();
        repository.findAllById(idList).forEach(g -> genresList.add(g));

        return genresList;
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public Genre getGenreById(String id) throws RecordNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found genre with id = %s", id)));
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public Genre saveGenre(Genre genre) {
        Genre savedGenre;

        try {
            savedGenre = repository.save(genre);
        } catch (Exception e) {
            return null;
        }

        return savedGenre;
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public boolean updateGenre(Genre genre) {
        Genre updatedGenre;

        try {
            updatedGenre = repository.save(genre);
        } catch (Exception e) {
            return false;
        }

        return genre.equals(updatedGenre);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public void deleteGenreById(String id) {
        repository.deleteById(id);
    }

    private List<Genre> getEmptyList() {
        return new ArrayList<>();
    }

}
