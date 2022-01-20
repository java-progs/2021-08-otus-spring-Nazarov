package ru.otus.homework.service;

import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

public interface GenreService {

    long getCountGenres();

    List<Genre> getAllGenres();

    List<Genre> getAllById(List<String> idList);

    Genre getGenreById(String id) throws RecordNotFoundException;

    Genre saveGenre(Genre genre);

    boolean updateGenre(Genre genre);

    void deleteGenreById(String id);
}
