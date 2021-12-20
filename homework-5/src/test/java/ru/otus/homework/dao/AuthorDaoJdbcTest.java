package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.otus.homework.domain.Author;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import(AuthorDaoJdbc.class)
@DisplayName("DAO для работы с авторами должен")
class AuthorDaoJdbcTest {

    public static final int EXPECTED_COUNT_AUTHORS = 7;
    public static final int EXISTING_AUTHOR_ID = 7;

    @Autowired
    private AuthorDaoJdbc dao;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedAuthorCount() {
        int actualAuthorCount = dao.count();
        assertThat(actualAuthorCount).isEqualTo(EXPECTED_COUNT_AUTHORS);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertAuthor() {
        val expectedAuthor = new Author( "Лермонтов", "Михаил", "Юрьевич");
        val actualAuthor = dao.insert(expectedAuthor);

        expectedAuthor.setId(actualAuthor.getId());

        assertThat(actualAuthor)
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedAuthorById() {
        val expectedAuthor = new Author(3, "Isakova", "Svetlana", null);
        val actualAuthor = dao.getById(expectedAuthor.getId());
        assertThat(actualAuthor)
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateAuthor() {
        var expectedAuthor = new Author(1, "Pushkin", "Aleksandr", "Sergeevich");
        var actualAuthor = dao.getById(expectedAuthor.getId());
        assertThat(actualAuthor)
                .usingRecursiveComparison().isEqualTo(expectedAuthor);

        expectedAuthor = new Author(1, "Pushkin", "Aleksandr", null);
        actualAuthor = dao.update(expectedAuthor);
        assertThat(actualAuthor)
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("Удаляет запись из БД")
    @Test
    void shouldDeleteAuthor() {
        assertThat(dao.deleteById(EXISTING_AUTHOR_ID)).isEqualTo(1);
        assertThatThrownBy(() -> dao.getById(EXISTING_AUTHOR_ID))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}