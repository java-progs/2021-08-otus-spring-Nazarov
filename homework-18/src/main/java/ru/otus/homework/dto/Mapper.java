package ru.otus.homework.dto;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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
    public static final String DELIMITER = ";";

    public BookDto toDto(Book book) {
        val id = book.getId();
        val isbn = book.getIsbn();
        val name = book.getName();

        val authorsId = book.getAuthorsList().stream().map(Author::getId).collect(Collectors.joining(DELIMITER));
        val genresId = book.getGenresList().stream().map(Genre::getId).collect(Collectors.joining(DELIMITER));

        return new BookDto(id, name, isbn, authorsId, genresId);
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    public Book toBook(BookDto bookDto) {
        val id = bookDto.getId();
        val isbn = bookDto.getIsbn();
        val name = bookDto.getName();
        List<Author> authors = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();

        authorRepository.findAllById(List.of(bookDto.getAuthorsId().split(DELIMITER))).forEach(author -> authors.add(author));
        genreRepository.findAllById(List.of(bookDto.getGenresId().split(DELIMITER))).forEach(genre -> genres.add(genre));
        
        return new Book(id, name, isbn, authors, genres, null);
    }

}
