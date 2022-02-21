package ru.otus.homework.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class AuthorRouter {

    @Bean
    public RouterFunction<ServerResponse> composedAuthorRoutes(AuthorHandler handler) {

        return route()
                .GET("/api/authors", accept(APPLICATION_JSON), handler::getAuthorsList)
                .GET("/api/authors/{id}", accept(APPLICATION_JSON), handler::getAuthor)
                .POST("/api/authors", accept(APPLICATION_JSON), handler::createAuthor)
                .PUT("/api/authors/{id}", accept(APPLICATION_JSON), handler::updateAuthor)
                .DELETE("/api/authors/{id}", accept(APPLICATION_JSON), handler::deleteAuthor)
                .build();
    }

}
