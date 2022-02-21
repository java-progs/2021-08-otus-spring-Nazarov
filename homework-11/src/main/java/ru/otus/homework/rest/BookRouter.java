package ru.otus.homework.rest;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.repositories.BookRepository;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class BookRouter {

    @Bean
    public RouterFunction<ServerResponse> composedBookRoutes(BookHandler handler) {

        return route()
                .GET("/api/books", accept(APPLICATION_JSON), handler::getBooksList)
                .GET("/api/books/{id}", accept(APPLICATION_JSON), handler::getBook)
                .POST("/api/books", accept(APPLICATION_JSON), handler::createBook)
                .PUT("/api/books/{id}", accept(APPLICATION_JSON), handler::updateBook)
                .DELETE("/api/books/{id}", accept(APPLICATION_JSON), handler::deleteBook)
                .build();
    }


}
