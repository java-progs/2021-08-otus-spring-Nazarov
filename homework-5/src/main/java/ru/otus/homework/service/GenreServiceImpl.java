package ru.otus.homework.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.GenreDao;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao dao;

    public GenreServiceImpl(GenreDao dao) {
        this.dao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCountGenres() {
        return dao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return dao.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Genre getGenreById(long id) throws RecordNotFoundException {
        try {
            return dao.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Not found genre with id = %d", id));
        }
    }

    @Override
    @Transactional
    public boolean addGenre(Genre genre) {
        try {
            dao.insert(genre);
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
            updatedGenre = dao.update(genre);
        } catch (Exception e) {
            return false;
        }

        return genre.equals(updatedGenre);
    }

    @Override
    @Transactional
    public boolean deleteGenreById(long id) {
        int deletedRows = 0;

        try {
            deletedRows = dao.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return deletedRows == 1;
    }
}
