package ru.otus.homework.dto;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookDto toDto(Book book) {
        val id = book.getId();
        val isbn = book.getIsbn();
        val name = book.getName();

        val authorsId = book.getAuthorsList().stream().map(Author::getId).collect(Collectors.toList());
        val genresId = book.getGenresList().stream().map(Genre::getId).collect(Collectors.toList());

        return new BookDto(id, name, isbn, authorsId, genresId);
    }

    public Book toBook(BookDto bookDto) {
        val id = bookDto.getId();
        val isbn = bookDto.getIsbn();
        val name = bookDto.getName();
        val authors = bookDto.getAuthorsId().stream().map(i -> authorRepository.findById(i).orElse(null))
                .filter(a -> a != null).collect(Collectors.toList());
        val genres = bookDto.getGenresId().stream().map(i -> genreRepository.findById(i).orElse(null))
                .filter(g -> g != null).collect(Collectors.toList());

        return new Book(id, name, isbn, authors, genres, null);
    }

}
