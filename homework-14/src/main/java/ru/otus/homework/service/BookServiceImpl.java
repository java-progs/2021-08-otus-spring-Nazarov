package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final NamedParameterJdbcOperations jdbcOperations;
    private final MongoTemplate mongoTemplate;

    private static final String SELECT_COMMENT_BY_BOOK = "SELECT * FROM comment where book_id = :id";

    @Override
    public BookMongo convertBookToBookMongo(Book book) {
        val bookMongo = new BookMongo();

        bookMongo.setName(book.getName());
        bookMongo.setIsbn(book.getIsbn());

        val bookComments = new ArrayList<CommentMongo>();

        jdbcOperations.query(SELECT_COMMENT_BY_BOOK,
                Map.of("id", book.getId()),
                (rs, rn) -> {
                    val comment = new CommentMongo();
                    comment.setAuthor(rs.getString("author"));
                    comment.setText(rs.getString("text"));
                    comment.setTime(rs.getObject("time", LocalDateTime.class));
                    return comment;
                }).forEach(c -> {
            c.setId(UUID.randomUUID().toString());
            bookComments.add(c);
        });

        bookMongo.setCommentsList(bookComments);

        val bookAuthorId = book.getAuthorsList().stream().map(a -> a.getId())
                .collect(Collectors.toList());
        val authorQuery = Query.query(Criteria.where("oldId").in(bookAuthorId));
        val bookAuthors = mongoTemplate.find(authorQuery, AuthorMongo.class);
        bookMongo.setAuthorsList(bookAuthors);

        val bookGenreId = book.getGenresList().stream().map(g -> g.getId())
                .collect(Collectors.toList());
        val genreQuery = Query.query(Criteria.where("oldId").in(bookGenreId));
        val bookGenres = mongoTemplate.find(genreQuery, GenreMongo.class);
        bookMongo.setGenresList(bookGenres);

        return bookMongo;
    }
}
