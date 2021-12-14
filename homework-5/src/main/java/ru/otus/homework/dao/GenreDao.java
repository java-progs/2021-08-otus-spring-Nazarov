package ru.otus.homework.dao;

import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.util.List;

public interface GenreDao {
    int count();

    Genre getById(long id);

    List<Genre> getAll();

    List<Genre> getActiveGenre();

    List<Genre> getBookGenres(Book book);

    Genre insert(Genre genre);

    Genre update(Genre genre);

    int deleteById(long id);
}
