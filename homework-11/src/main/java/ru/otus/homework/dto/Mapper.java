package ru.otus.homework.dto;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.GenreRepository;

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

    public Mono<Book> toBook(BookDto bookDto) {
        val id = bookDto.getId();
        val isbn = bookDto.getIsbn();
        val name = bookDto.getName();

        val authorList = Flux.fromIterable(bookDto.getAuthorsId()).flatMap(authorRepository::findById).collectList();
        val genreList = Flux.fromIterable(bookDto.getGenresId()).flatMap(genreRepository::findById).collectList();

        return Mono.zip(authorList, genreList,
                (authors, genres) -> new Book(id, name, isbn, authors, genres, null));

    }

}
