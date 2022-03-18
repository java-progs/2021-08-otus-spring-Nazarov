package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository repository;

    @Override
    @Transactional(readOnly = true)
    public long getCountGenres() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Genre getGenreById(long id) throws RecordNotFoundException {
        return repository.findById(id)
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
    public void deleteGenreById(long id) {
        repository.deleteById(id);
    }

}
