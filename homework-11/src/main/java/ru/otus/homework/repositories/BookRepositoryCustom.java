package ru.otus.homework.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.ObjectNotFoundException;

public interface BookRepositoryCustom {

    Flux<Book> findByAuthor(String authorId);

    Flux<Book> findByGenre(String genreId);

    Mono<Comment> findComment(String bookId, String commentId) throws ObjectNotFoundException;

    Mono<Long> getCountByAuthor(String authorId);

    Mono<Long> getCountByGenre(String genreId);

    Mono<Comment> addComment(String bookId, Comment comment);

    Mono<Comment> updateComment(String bookId, Comment comment);

    Mono<Boolean> deleteComment(String bookId, String commentId);

}
