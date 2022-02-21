package ru.otus.homework.rest;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.repositories.BookRepository;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class BookHandler {

    private final BookRepository repository;
    private final Mapper mapper;

    BookHandler(BookRepository repository, Mapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    Mono<ServerResponse> getBooksList(ServerRequest request) {
        return repository.findAll().map(book -> mapper.toDto(book)).collectList()
                .flatMap(books -> ok().contentType(APPLICATION_JSON).body(fromValue(books)));
    }

    Mono<ServerResponse> getBook(ServerRequest request) {
        return repository.findById(request.pathVariable("id"))
                .flatMap(book -> ok().contentType(APPLICATION_JSON).body(fromValue(mapper.toDto(book))))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> createBook(ServerRequest request) {
        Mono<BookDto> monoBookDto = request.bodyToMono(BookDto.class);
        return monoBookDto.flatMap(bookDto -> mapper.toBook(bookDto))
                .flatMap(book -> repository.save(book))
                .flatMap(savedBook -> created(URI.create("/api/books/" + savedBook.getId()))
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(mapper.toDto(savedBook)), BookDto.class)
                ).switchIfEmpty(badRequest().build());
    }

    Mono<ServerResponse> updateBook(ServerRequest request) {
        val pathId = request.pathVariable("id");
        Mono<BookDto> monoBookDto = request.bodyToMono(BookDto.class);
        return monoBookDto.flatMap(bookDto -> mapper.toBook(bookDto))
                .flatMap(book -> {
                    if (book.getId().equals(pathId)) {
                        return repository.existsById(book.getId())
                                .flatMap(bookExist -> bookExist ? repository.save(book) : Mono.empty());
                    }
                    return Mono.empty();
                }).flatMap(savedBok -> ok().contentType(APPLICATION_JSON)
                        .body(Mono.just(mapper.toDto(savedBok)), BookDto.class))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> deleteBook(ServerRequest request) {
        return repository.deleteById(request.pathVariable("id"))
                .flatMap(result -> ok().contentType(APPLICATION_JSON).build());
    }

}
