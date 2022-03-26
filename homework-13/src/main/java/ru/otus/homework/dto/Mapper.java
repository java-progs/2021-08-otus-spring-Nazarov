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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    public static final String DELIMITER = ";";

    public BookDto toDto(Book book) {
        val id = book.getId();
        val isbn = book.getIsbn();
        val name = book.getName();

        val authorsId = book.getAuthorsList().stream().map(Author::getId).map(i -> Long.toString(i)).collect(Collectors.joining(DELIMITER));
        val genresId = book.getGenresList().stream().map(Genre::getId).map(i -> Long.toString(i)).collect(Collectors.joining(DELIMITER));

        return new BookDto(Long.toString(id), name, isbn, authorsId, genresId);
    }

    public Book toBook(BookDto bookDto) {
        val id = Long.parseLong(bookDto.getId());
        val isbn = bookDto.getIsbn();
        val name = bookDto.getName();
        List<Author> authors = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();

        val authorsId = Arrays.stream(bookDto.getAuthorsId().split(DELIMITER))
                .filter(i -> i.length() > 0).map(i -> Long.parseLong(i)).collect(Collectors.toList());
        val genresId = Arrays.stream(bookDto.getGenresId().split(DELIMITER))
                .filter(i -> i.length() > 0).map(i -> Long.parseLong(i)).collect(Collectors.toList());

        authorRepository.findAllById(authorsId).forEach(author -> authors.add(author));
        genreRepository.findAllById(genresId).forEach(genre -> genres.add(genre));

        return new Book(id, name, isbn, authors, genres);
    }

}
