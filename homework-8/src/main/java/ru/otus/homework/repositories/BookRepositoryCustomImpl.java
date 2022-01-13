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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public BookRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Comment findComment(String bookId, String commentId) {
        val matchBook = new MatchOperation(Criteria.where("_id").is(new ObjectId(bookId)));
        val unwind = unwind("commentsList");
        val matchComment = new MatchOperation(Criteria.where("commentsList._id").is(commentId));
        val project = project().andExclude("_id")
                .and("commentsList._id").as("_id")
                .and("commentsList.author").as("author")
                .and("commentsList.time").as("time")
                .and("commentsList.text").as("text");
        val aggregation = newAggregation(matchBook, unwind, matchComment, project);
        val result = mongoTemplate.aggregate(aggregation, Book.class, Comment.class).getMappedResults().get(0);

        return result;
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
