package ru.otus.homework.service;

import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface GenreService {

    int getCountGenres();

    List<Genre> getAllGenres();

    Genre getGenreById(long id) throws RecordNotFoundException;

    boolean addGenre(Genre genre);

    boolean updateGenre(Genre genre);

    boolean deleteGenreById(long id);
}
