package ru.otus.homework.rest;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.ErrorMessage;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class AuthorRouter {

    @Bean
    public RouterFunction<ServerResponse> composedAuthorRoutes(AuthorRepository authorRepository,
                                                               BookRepository bookRepository) {

        val handler = new AuthorHandler(authorRepository, bookRepository);

        return route()
                .GET("/api/authors", accept(APPLICATION_JSON), handler::getAuthorsList)
                .GET("/api/authors/{id}", accept(APPLICATION_JSON), handler::getAuthor)
                .POST("/api/authors", accept(APPLICATION_JSON), handler::createAuthor)
                .PUT("/api/authors/{id}", accept(APPLICATION_JSON), handler::updateAuthor)
                .DELETE("/api/authors/{id}", accept(APPLICATION_JSON), handler::deleteAuthor)
                .build();
    }

    static class AuthorHandler {

        private AuthorRepository authorRepository;
        private BookRepository bookRepository;

        AuthorHandler(AuthorRepository authorRepository, BookRepository bookRepository) {
            this.authorRepository = authorRepository;
            this.bookRepository = bookRepository;
        }

        Mono<ServerResponse> getAuthorsList(ServerRequest request) {
            return authorRepository.findAll().collectList()
                    .flatMap(authors -> ok().contentType(APPLICATION_JSON).body(fromValue(authors)));
        }

        Mono<ServerResponse> getAuthor(ServerRequest request) {
            return authorRepository.findById(request.pathVariable("id"))
                    .flatMap(author -> ok().contentType(APPLICATION_JSON).body(fromValue(author)))
                    .switchIfEmpty(notFound().build());
        }

        Mono<ServerResponse> createAuthor(ServerRequest request) {
            Mono<Author> monoAuthor = request.bodyToMono(Author.class);
            return monoAuthor.flatMap(authorRepository::save)
                    .flatMap(savedAuthor -> created(URI.create("/api/authors/" + savedAuthor.getId()))
                            .contentType(APPLICATION_JSON)
                            .body(Mono.just(savedAuthor), Author.class)
                    ).switchIfEmpty(badRequest().build());
        }

        Mono<ServerResponse> updateAuthor(ServerRequest request) {
            val pathId = request.pathVariable("id");
            Mono<Author> monoAuthor = request.bodyToMono(Author.class);
            return monoAuthor.flatMap(author -> {
                        if (author.getId().equals(pathId)) {
                            return authorRepository.existsById(author.getId())
                                    .flatMap(result ->
                                            result ? authorRepository.save(author) : Mono.empty());
                        }
                        return Mono.empty();
                    })
                    .flatMap(savedAuthor -> ok().contentType(APPLICATION_JSON)
                            .body(Mono.just(savedAuthor), Author.class)
                    )
                    .switchIfEmpty(notFound().build());
        }

        Mono<ServerResponse> deleteAuthor(ServerRequest request) {
            val id = request.pathVariable("id");
            return bookRepository.getCountByAuthor(id)
                    .flatMap(result -> {
                        if (result > 0) {
                            return Mono.error(new ViolationOfConstraintException("Delete books before delete author"));
                        }
                        return authorRepository.deleteById(id);
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
}
