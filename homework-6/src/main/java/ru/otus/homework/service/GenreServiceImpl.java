package ru.otus.homework.service;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.GenreRepository;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository repository;

    public GenreServiceImpl(GenreRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public long getCountGenres() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return repository.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Genre getGenreById(long id) throws RecordNotFoundException {
        return repository.getById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found genre with id = %d", id)));
    }

    @Override
    @Transactional
    public boolean saveGenre(Genre genre) {
        try {
            repository.save(genre);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateGenre(Genre genre) {
        Genre updatedGenre;

        try {
            updatedGenre = repository.save(genre);
        } catch (Exception e) {
            return false;
        }

        return genre.equals(updatedGenre);
    }

    @Override
    @Transactional
    public boolean deleteGenreById(long id) {
        var deletedRows = 0;

        try {
            deletedRows = repository.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return deletedRows == 1;
    }

}
