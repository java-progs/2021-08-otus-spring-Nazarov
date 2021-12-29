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
@DisplayName("Репозиторий для работы с комментариями должен")
class CommentRepositoryJpaTest {

    private final long EXISTING_BOOK_ID = 2;
    private final long COUNT_COMMENTS_FOR_EXISTING_BOOK = 2;

    @Autowired
    private CommentRepository repository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertComment() {
        val book = bookRepository.findById(EXISTING_BOOK_ID).get();
        val newComment = new Comment(0, "Reader", getCurrentTime(), "Text comment", book);
        val allComments = repository.findAll();
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
        val book = bookRepository.findById(EXISTING_BOOK_ID).get();
        val newComment = new Comment(0, "Reader", getCurrentTime(), "Text comment", book);
        val allComments = repository.findAll();
        assertThat(allComments).allMatch(
                c -> !(c.getAuthor() + c.getText() + c.getBook().getId())
                        .equals(newComment.getBook() + newComment.getText() + newComment.getBook().getId()));

        em.persist(newComment);

        val optionalActualComment = repository.findById(newComment.getId());

        assertThat(optionalActualComment).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(newComment);
    }

    @DisplayName("Возвращать комментарии к книге по ее id")
    @Test
    void shouldReturnAllBookComments() {

        val commentsList = repository.findAllByBookId(EXISTING_BOOK_ID);

        assertThat(commentsList).allMatch(c -> c.getId() > 0L)
                .allMatch(c -> c.getAuthor().length() > 0)
                .allMatch(c -> c.getTime().toString().length() > 0)
                .allMatch(c -> c.getText().length() > 0)
                .allMatch(c -> c.getBook().getId() == EXISTING_BOOK_ID)
                .size().isEqualTo(COUNT_COMMENTS_FOR_EXISTING_BOOK);
    }

    private Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

}