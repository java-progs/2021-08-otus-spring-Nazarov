package ru.otus.homework.service;

import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.ObjectNotFoundException;

import java.util.List;

public interface GenreService {

    long getCountGenres();

    List<Genre> getAllGenres();

    List<Genre> getAllById(List<String> idList);

    Genre getGenreById(String id) throws ObjectNotFoundException;

    Genre saveGenre(Genre genre);

    boolean updateGenre(Genre genre);

    void deleteGenreById(String id);

    boolean existById(String id);
}
