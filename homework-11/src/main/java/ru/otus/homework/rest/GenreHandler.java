package ru.otus.homework.rest;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.ErrorMessage;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class GenreHandler {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    GenreHandler(GenreRepository repository, BookRepository bookRepository) {
        this.genreRepository = repository;
        this.bookRepository = bookRepository;
    }

    Mono<ServerResponse> getGenresList(ServerRequest request) {
        return genreRepository.findAll().collectList()
                .flatMap(genres -> ok().contentType(APPLICATION_JSON).body(fromValue(genres)));
    }

    Mono<ServerResponse> getGenre(ServerRequest request) {
        return genreRepository.findById(request.pathVariable("id"))
                .flatMap(genre -> ok().contentType(APPLICATION_JSON).body(fromValue(genre)))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> createGenre(ServerRequest request) {
        Mono<Genre> monoGenre = request.bodyToMono(Genre.class);
        return monoGenre.flatMap(genreRepository::save)
                .flatMap(savedGenre -> created(URI.create("/api/genres/" + savedGenre.getId()))
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(savedGenre), Genre.class)
                ).switchIfEmpty(badRequest().build());
    }

    Mono<ServerResponse> updateGenre(ServerRequest request) {
        val pathId = request.pathVariable("id");
        Mono<Genre> monoGenre = request.bodyToMono(Genre.class);
        return monoGenre.flatMap(genre -> {
                    if (genre.getId().equals(pathId)) {
                        return genreRepository.existsById(genre.getId())
                                .flatMap(result ->
                                        result ? genreRepository.save(genre) : Mono.empty());
                    }
                    return Mono.empty();
                })
                .flatMap(savedGenre -> ok().contentType(APPLICATION_JSON)
                        .body(Mono.just(savedGenre), Genre.class)
                )
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> deleteGenre(ServerRequest request) {
        val id = request.pathVariable("id");
        return bookRepository.getCountByGenre(id)
                .flatMap(result -> {
                    if (result > 0) {
                        return Mono.error(new ViolationOfConstraintException("Delete books before delete genre"));
                    }
                    return genreRepository.deleteById(request.pathVariable("id"));
                })
                .map(voidResult -> "ok")
                .onErrorResume(ViolationOfConstraintException.class, t -> Mono.just(t.getMessage()))
                .flatMap(result -> {
                    if (!result.equals("ok")) {
                        val error = new ErrorMessage(result);
                        return badRequest().contentType(APPLICATION_JSON).body(fromValue(error));
                    }
                    return ok().contentType(APPLICATION_JSON).build();
                });
    }
}
