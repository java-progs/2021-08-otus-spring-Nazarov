package ru.otus.homework.repositories;

import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.ObjectNotFoundException;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    public BookRepositoryCustomImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Book> findByAuthor(String authorId) {
        val query = Query.query(Criteria.where("authorsList.$id").is(new ObjectId(authorId)));
        return mongoTemplate.find(query, Book.class);
    }

    @Override
    public Flux<Book> findByGenre(String genreId) {
        val query = Query.query(Criteria.where("genresList.$id").is(new ObjectId(genreId)));
        return mongoTemplate.find(query, Book.class);
    }

    @Override
    public Mono<Comment> findComment(String bookId, String commentId) throws ObjectNotFoundException {
        val matchBook = new MatchOperation(Criteria.where("_id").is(new ObjectId(bookId)));
        val unwind = unwind("commentsList");
        val matchComment = new MatchOperation(Criteria.where("commentsList._id").is(commentId));
        val project = project().andExclude("_id")
                .and("commentsList._id").as("_id")
                .and("commentsList.author").as("author")
                .and("commentsList.time").as("time")
                .and("commentsList.text").as("text");
        val aggregation = newAggregation(matchBook, unwind, matchComment, project);

        try {
            return mongoTemplate.aggregate(aggregation, Book.class, Comment.class).next();
        } catch (IndexOutOfBoundsException e) {
            throw new ObjectNotFoundException(String.format("No comment with id %s found for book with id %s", commentId, bookId));
        }
    }

    @Override
    public Mono<Long> getCountByAuthor(String authorId) {
        val query = Query.query(Criteria.where("authorsList.$id").is(new ObjectId(authorId)));
        return mongoTemplate.count(query, Book.class);
    }

    @Override
    public Mono<Long> getCountByGenre(String genreId) {
        val query = Query.query(Criteria.where("genresList.$id").is(new ObjectId(genreId)));
        return mongoTemplate.count(query, Book.class);
    }

    @Override
    public Mono<Comment> addComment(String bookId, Comment comment) {
        val bookQuery = Query.query(Criteria.where("_id").is(new ObjectId(bookId)));
        val update = new Update().push("commentsList", comment);
        return mongoTemplate.updateFirst(bookQuery, update, Book.class)
                .flatMap(result -> (result.getModifiedCount() > 0) ? Mono.just(comment) : Mono.empty());
    }

    @Override
    public Mono<Comment> updateComment(String bookId, Comment comment) {
        return deleteComment(bookId, comment.getId())
                .flatMap(result -> {
                    if (result) return addComment(bookId, comment);
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Boolean> deleteComment(String bookId, String commentId) {
        val bookQuery = Query.query(Criteria.where("_id").is(new ObjectId(bookId)));
        val commentQuery = Query.query(Criteria.where("_id").is(commentId));
        val update = new Update().pull("commentsList", commentQuery);
        return mongoTemplate.updateFirst(bookQuery, update, Book.class).map(result -> result.getModifiedCount() > 0);
    }

}
