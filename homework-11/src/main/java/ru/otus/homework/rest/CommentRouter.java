package ru.otus.homework.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class CommentRouter {

    @Bean
    public RouterFunction<ServerResponse> composedCommentRoutes(CommentHandler handler) {

        return route()
                .GET("/api/books/{book_id}/comments", accept(APPLICATION_JSON), handler::getCommentsList)
                .GET("/api/books/{book_id}/comments/{id}", accept(APPLICATION_JSON), handler::getComment)
                .POST("/api/books/{book_id}/comments", accept(APPLICATION_JSON), handler::createComment)
                .PUT("/api/books/{book_id}/comments/{id}", accept(APPLICATION_JSON), handler::updateComment)
                .DELETE("/api/books/{book_id}/comments/{id}", accept(APPLICATION_JSON), handler::deleteComment)
                .build();
    }

}
