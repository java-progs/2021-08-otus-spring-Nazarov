package ru.otus.homework.service;

import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface GenreService {

    long getCountGenres();

    List<Genre> getAllGenres();

    Genre getGenreById(long id) throws RecordNotFoundException;

    boolean saveGenre(Genre genre);

    boolean updateGenre(Genre genre);

    void deleteGenreById(long id);
}
