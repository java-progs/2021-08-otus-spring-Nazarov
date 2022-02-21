package ru.otus.homework.rest;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.repositories.BookRepository;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class CommentHandler {

    private final BookRepository repository;

    CommentHandler(BookRepository repository) {
        this.repository = repository;
    }

    Mono<ServerResponse> getCommentsList(ServerRequest request) {
        val bookId = request.pathVariable("book_id");

        return repository.findById(bookId)
                .map(book -> book.getCommentsList())
                .onErrorReturn(NullPointerException.class, List.of())
                .flatMap(comments -> ok().contentType(APPLICATION_JSON).body(fromValue(comments)))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> getComment(ServerRequest request) {
        val bookId = request.pathVariable("book_id");
        val commentId = request.pathVariable("id");

        return repository.findComment(bookId, commentId)
                .flatMap(comment -> ok().contentType(APPLICATION_JSON).body(fromValue(comment)))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> createComment(ServerRequest request) {
        val bookId = request.pathVariable("book_id");
        val commentId = UUID.randomUUID().toString();
        Mono<Comment> monoComment = request.bodyToMono(Comment.class);

        return monoComment.map(comment -> {
                    comment.setId(commentId);
                    comment.setTime(getTime());
                    return comment;
                })
                .flatMap(comment -> repository.addComment(bookId, comment))
                .flatMap(savedComment ->
                        created(URI.create("/api/books/" + bookId + "/comments/" + savedComment.getId()))
                                .contentType(APPLICATION_JSON)
                                .body(fromValue(savedComment)))
                .switchIfEmpty(badRequest().build());
    }

    Mono<ServerResponse> updateComment(ServerRequest request) {
        val bookId = request.pathVariable("book_id");
        val commentId = request.pathVariable("id");

        Mono<Comment> monoComment = request.bodyToMono(Comment.class);

        return monoComment.map(comment -> {
                    comment.setTime(getTime());
                    return comment;
                })
                .flatMap(comment -> {
                    if (comment.getId().equals(commentId)) {
                        return repository.updateComment(bookId, comment);
                    }
                    return Mono.empty();
                })
                .flatMap(savedComment -> ok().contentType(APPLICATION_JSON)
                        .body(fromValue(savedComment)))
                .switchIfEmpty(badRequest().build());
    }

    Mono<ServerResponse> deleteComment(ServerRequest request) {
        val bookId = request.pathVariable("book_id");
        val commentId = request.pathVariable("id");

        return repository.deleteComment(bookId, commentId)
                .flatMap(result -> ok().contentType(APPLICATION_JSON).build());
    }

    private static LocalDateTime getTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
