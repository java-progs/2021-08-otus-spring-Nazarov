package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.homework.domain.Comment;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CommentRepositoryJpa.class, BookRepositoryJpa.class, AuthorRepositoryJpa.class, GenreRepositoryJpa.class})
@DisplayName("Репозиторий для работы с комментариями должен")
class CommentRepositoryJpaTest {

    public static final int EXPECTED_COUNT_COMMENTS = 3;
    public static final long EXISTING_COMMENT_ID = 1;

    public static final long EXISTING_BOOK_ID = 2;
    public static final long EXISTING_BOOK_COUNT_COMMENTS = 2;

    @Autowired
    private CommentRepositoryJpa repository;

    @Autowired
    private BookRepositoryJpa bookRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedCommentsCount() {
        long actualGenresCount = repository.count();
        assertThat(actualGenresCount).isEqualTo(EXPECTED_COUNT_COMMENTS);
    }

    @DisplayName("Возвращать все записи из БД")
    @Test
    void shouldReturnAllComments() {
        val actualCommentsList = repository.getAll();
        assertThat(actualCommentsList).isNotEmpty()
                .allMatch(c -> c.getId() > 0)
                .allMatch(c -> c.getAuthor().length() > 0)
                .allMatch(c -> c.getTime().toString().length() > 0)
                .allMatch(c -> c.getText().length() > 0)
                .allMatch(c -> c.getBook().getId() > 0)
                .size().isEqualTo(EXPECTED_COUNT_COMMENTS);
    }

    @DisplayName("Должен вернуть все комментарии к книге")
    @Test
    void shouldReturnAllBookComments() {
        val actualCommentList = repository.getBookComments(EXISTING_BOOK_ID);
        assertThat(actualCommentList).isNotEmpty()
                .allMatch(c -> c.getId() > 0)
                .allMatch(c -> c.getAuthor().length() > 0)
                .allMatch(c -> c.getTime().toString().length() > 0)
                .allMatch(c -> c.getBook().getId() == EXISTING_BOOK_ID)
                .size().isEqualTo(EXISTING_BOOK_COUNT_COMMENTS);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertComment() {
        val book = bookRepository.getById(EXISTING_BOOK_ID).get();
        val newComment = new Comment(0, "Reader", getCurrentTime(), "Text comment", book);
        val allComments = repository.getAll();
        assertThat(allComments).allMatch(
                c -> !(c.getAuthor() + c.getText() + c.getBook().getId())
                        .equals(newComment.getBook() + newComment.getText() + newComment.getBook().getId()));

        val actualComment = repository.save(newComment);
        val expectedComment = em.find(Comment.class, actualComment.getId());

        assertThat(actualComment)
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedCommentById() {
        val optionalComment = repository.getById(EXISTING_COMMENT_ID);
        val expectedComment = em.find(Comment.class, EXISTING_COMMENT_ID);

        assertThat(optionalComment).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateComment() {
        var optionalComment = repository.getById(EXISTING_COMMENT_ID);
        assertThat(optionalComment).isPresent();

        val actualComment = optionalComment.get();
        actualComment.setText("Updated comment text");
        actualComment.setTime(getCurrentTime());
        repository.save(actualComment);

        val expectedComment = em.find(Comment.class, EXISTING_COMMENT_ID);

        assertThat(actualComment)
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("Удаляет запись из БД")
    @Test
    void shouldDeleteComment() {
        assertThat(repository.deleteById(EXISTING_COMMENT_ID)).isEqualTo(1);
        assertThat(repository.getById(EXISTING_COMMENT_ID).isEmpty());
    }

    private Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }
}