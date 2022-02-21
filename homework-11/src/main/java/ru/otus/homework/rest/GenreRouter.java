package ru.otus.homework.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class GenreRouter {
    
    @Bean
    public RouterFunction<ServerResponse> composedGenreRoutes(GenreHandler handler) {

        return route()
                .GET("/api/genres", accept(APPLICATION_JSON), handler::getGenresList)
                .GET("/api/genres/{id}", accept(APPLICATION_JSON), handler::getGenre)
                .POST("/api/genres", accept(APPLICATION_JSON), handler::createGenre)
                .PUT("/api/genres/{id}", accept(APPLICATION_JSON), handler::updateGenre)
                .DELETE("/api/genres/{id}", accept(APPLICATION_JSON), handler::deleteGenre)
                .build();
    }

}
