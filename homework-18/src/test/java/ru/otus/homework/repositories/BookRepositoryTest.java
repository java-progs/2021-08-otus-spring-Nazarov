package ru.otus.homework.repositories;

import com.github.cloudyrock.spring.v5.EnableMongock;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@ComponentScan({"ru.otus.homework.changelogs", "ru.otus.homework.repositories"})
@DisplayName("Репозиторий для работы с книгами должен ")
@EnableConfigurationProperties
@EnableMongock
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private MongoOperations operations;

    @DisplayName(" обновлять книгу")
    @Test
    void shouldUpdatedBook() {
        val books = repository.findAll();
        val expectedBook = books.get(0);
        expectedBook.setName(expectedBook.getName() + " (Update)");

        val actualBook = repository.save(expectedBook);

        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("получать комментарий к книге")
    @Test
    void shouldFindComment() {
        val book = repository.findAll().get(1);
        val expectedComment = book.getCommentsList().get(0);
        val actualComment = repository.findComment(book.getId(), expectedComment.getId());
        assertThat(actualComment).usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("генерировать исключении при поиске несуществующего комментария")
    @Test
    void shouldThrowException() {
        val book = repository.findAll().get(0);
        val randomCommentId = UUID.randomUUID().toString();
        assertThatThrownBy(() -> repository.findComment(book.getId(), randomCommentId))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @DisplayName("добавлять комментарий")
    @Test
    void shouldAddComment() {
        val book = repository.findAll().get(1);
        val countComment = book.getCommentsList().size();
        val expectedComment = new Comment(UUID.randomUUID().toString(), "Admin",
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), "Test comment");
        val result = repository.addComment(book.getId(), expectedComment);
        assertThat(result).isTrue();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val actualBook = operations.find(query, Book.class).get(0);

        assertThat(actualBook.getCommentsList().size()).isEqualTo(countComment + 1);
        assertThat(actualBook.getCommentsList()).contains(expectedComment);
    }

    @DisplayName("обновлять комментарий")
    @Test
    public void shouldUpdateComment() {
        val book = repository.findAll().get(1);
        val commentsList = book.getCommentsList();
        val comment = commentsList.get(0);
        val expectedComment = new Comment(comment.getId(), comment.getAuthor(),
                comment.getTime(), comment.getText() + " (Updated)");
        val result = repository.updateComment(book.getId(), expectedComment);
        assertThat(result).isTrue();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val actualBook = operations.find(query, Book.class).get(0);
        assertThat(actualBook.getCommentsList()).doesNotContain(comment);
        assertThat(actualBook.getCommentsList()).contains(expectedComment);
    }

    @DisplayName("удалять комментарий")
    @Test
    public void shouldDeleteComment() {
        val book = repository.findAll().get(1);
        val commentsList = book.getCommentsList();
        val comment = commentsList.get(0);
        val countComment = commentsList.size();
        val result = repository.deleteComment(book.getId(), comment.getId());
        assertThat(result).isTrue();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val actualBook = operations.find(query, Book.class).get(0);
        assertThat(actualBook.getCommentsList().size()).isEqualTo(countComment - 1);
        assertThat(actualBook.getCommentsList()).doesNotContain(comment);
    }
}
