package ru.otus.homework.repositories;

import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.ObjectNotFoundException;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public BookRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Book> findByAuthor(String authorId) {
        val query = Query.query(Criteria.where("authorsList.$id").is(new ObjectId(authorId)));
        return mongoTemplate.find(query, Book.class);
    }

    @Override
    public List<Book> findByGenre(String genreId) {
        val query = Query.query(Criteria.where("genresList.$id").is(new ObjectId(genreId)));
        return mongoTemplate.find(query, Book.class);
    }

    @Override
    public Comment findComment(String bookId, String commentId) throws ObjectNotFoundException {
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
            val result = mongoTemplate.aggregate(aggregation, Book.class, Comment.class).getMappedResults().get(0);
            return result;
        } catch (IndexOutOfBoundsException e) {
            throw new ObjectNotFoundException(String.format("No comment with id %s found for book with id %s", commentId, bookId));
        }
    }

    @Override
    public Long getCountByAuthor(String authorId) {
        val query = Query.query(Criteria.where("authorsList.$id").is(new ObjectId(authorId)));
        return mongoTemplate.count(query, Book.class);
    }

    @Override
    public Long getCountByGenre(String genreId) {
        val query = Query.query(Criteria.where("genresList.$id").is(new ObjectId(genreId)));
        return mongoTemplate.count(query, Book.class);
    }

    @Override
    public boolean addComment(String bookId, Comment comment) {
        val bookQuery = Query.query(Criteria.where("_id").is(new ObjectId(bookId)));
        val update = new Update().push("commentsList", comment);
        val updatedCount = mongoTemplate.updateFirst(bookQuery, update, Book.class).getModifiedCount();

        return updatedCount > 0;
    }

    @Override
    public boolean updateComment(String bookId, Comment comment) {
        if (!deleteComment(bookId, comment.getId())) {
            return false;
        }

        return addComment(bookId, comment);
    }

    @Override
    public boolean deleteComment(String bookId, String commentId) {
        val bookQuery = Query.query(Criteria.where("_id").is(new ObjectId(bookId)));
        val commentQuery = Query.query(Criteria.where("_id").is(commentId));
        val update = new Update().pull("commentsList", commentQuery);
        val deletedCount = mongoTemplate.updateFirst(bookQuery, update, Book.class).getModifiedCount();

        return deletedCount > 0;
    }

}
