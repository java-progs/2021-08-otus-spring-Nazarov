package ru.otus.homework.repositories;

import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.test.StepVerifier;
import ru.otus.homework.configuration.MongockConfiguration;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ComponentScan({"ru.otus.homework.changelogs", "ru.otus.homework.repositories"})
@Import(MongockConfiguration.class)
@DisplayName("Репозиторий для работы с книгами должен ")
@EnableConfigurationProperties
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private MongoOperations operations;

    @DisplayName(" обновлять книгу")
    @Test
    void shouldUpdatedBook() {
        val book = repository.findAll().blockFirst();
        book.setName(book.getName() + " (Update)");

        val monoBook = repository.save(book);

        StepVerifier
                .create(monoBook)
                .assertNext(updatedBook -> assertTrue(updatedBook.getName().contains("(Update)")))
                .expectComplete()
                .verify();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val actualBook = operations.findOne(query, Book.class);
        assertThat(book).isEqualTo(actualBook);

    }

    @DisplayName("получать комментарий к книге")
    @Test
    void shouldFindComment() {
        val book = repository.findAll().filter(b -> b.getCommentsList() != null).blockFirst();
        val expectedComment = book.getCommentsList().get(0);

        val monoComment = repository.findComment(book.getId(), expectedComment.getId());

        StepVerifier
                .create(monoComment)
                .assertNext(comment -> assertEquals(expectedComment, comment))
                .expectComplete()
                .verify();
    }

    @DisplayName("возвращать empty Mono при поиске несуществующего комментария")
    @Test
    void shouldReturnEmptyMono() {
        val book = repository.findAll().filter(b -> b.getCommentsList() != null).blockFirst();
        val randomCommentId = UUID.randomUUID().toString();

        val monoComment = repository.findComment(book.getId(), randomCommentId);

        StepVerifier
                .create(monoComment)
                .expectComplete()
                .verify();
    }

    @DisplayName("добавлять комментарий")
    @Test
    void shouldAddComment() {
        val book = repository.findAll().filter(b -> b.getCommentsList() == null).blockFirst();

        val newComment = new Comment(UUID.randomUUID().toString(), "Admin",
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), "Test comment");
        val monoAdd = repository.addComment(book.getId(), newComment);

        StepVerifier
                .create(monoAdd)
                .assertNext(comment -> comment.equals(newComment))
                .expectComplete()
                .verify();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val expectCommentsCount = operations.findOne(query, Book.class).getCommentsList().size();

        assertThat(expectCommentsCount).isEqualTo(1);
    }

    @DisplayName("обновлять комментарий")
    @Test
    void shouldUpdateComment() {
        val book = repository.findAll().filter(b -> b.getCommentsList() != null).blockFirst();
        val comment = book.getCommentsList().get(0);
        comment.setText(comment.getText() + " (Updated)");
        val monoUpdate = repository.updateComment(book.getId(), comment);

        StepVerifier
                .create(monoUpdate)
                .assertNext(updatedComment -> {
                    updatedComment.getId().equals(comment.getId());
                    updatedComment.getText().contains(" (Updated)");
                    updatedComment.getTime().isEqual(comment.getTime());
                })
                .expectComplete()
                .verify();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val expectComment = operations.findOne(query, Book.class).getCommentsList();

        assertThat(expectComment).contains(comment);
    }

    @DisplayName("удалять комментарий")
    @Test
    public void shouldDeleteComment() {
        val book = repository.findAll().filter(b -> b.getCommentsList() != null).blockFirst();
        val comment = book.getCommentsList().get(0);

        val monoDelete = repository.deleteComment(book.getId(), comment.getId());

        StepVerifier
                .create(monoDelete)
                .assertNext(result -> result.equals(true))
                .expectComplete()
                .verify();

        val query = Query.query(Criteria.where("_id").is(book.getId()));
        val expectComments = operations.findOne(query, Book.class).getCommentsList();

        assertThat(expectComments).doesNotContain(comment);
    }

    @DisplayName("возвращать количество книг автора")
    @Test
    public void shouldReturnCountBooksByAuthor() {
        val books = operations.findAll(Book.class);
        val author = books.get(0).getAuthorsList().get(0);

        val query = Query.query(Criteria.where("authorsList.$id").is(new ObjectId(author.getId())));
        val expectCountBooks = operations.count(query, Book.class);

        val monoBookCounts = repository.getCountByAuthor(author.getId());

        StepVerifier
                .create(monoBookCounts)
                .assertNext(count -> count.equals(expectCountBooks))
                .expectComplete()
                .verify();

    }

    @DisplayName("возвращать количество книг жанра")
    @Test
    public void shouldReturnCountBooksByGenre() {
        val books = operations.findAll(Book.class);
        val genre = books.get(0).getGenresList().get(0);

        val query = Query.query(Criteria.where("genresList.$id").is(new ObjectId(genre.getId())));
        val expectCountBooks = operations.count(query, Genre.class);

        val monoBookCounts = repository.getCountByGenre(genre.getId());

        StepVerifier
                .create(monoBookCounts)
                .assertNext(count -> count.equals(expectCountBooks))
                .expectComplete()
                .verify();

    }
}
