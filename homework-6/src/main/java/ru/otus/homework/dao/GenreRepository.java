package ru.otus.homework.dao;

import ru.otus.homework.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    long count();

    Optional<Genre> getById(long id);

    List<Genre> getAll();

    Genre save(Genre genre);

    int deleteById(long id);
}
