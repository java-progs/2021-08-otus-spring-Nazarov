package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Book {
    private long id;
    @NonNull
    private final String name;
    private final String isbn;
    private final List<Author> authorsList;
    private final List<Genre> genresList;
}
