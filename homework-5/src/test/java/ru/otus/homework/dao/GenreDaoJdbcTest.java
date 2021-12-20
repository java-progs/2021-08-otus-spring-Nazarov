package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.otus.homework.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(GenreDaoJdbc.class)
@DisplayName("DAO для работы с жанрами должен")
class GenreDaoJdbcTest {

    public static final int EXPECTED_COUNT_GENRES = 4;
    public static final int EXISTING_GENRE_ID = 4;

    @Autowired
    private GenreDaoJdbc dao;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedGenreCount() {
        int actualGenreCount = dao.count();
        assertThat(actualGenreCount).isEqualTo(EXPECTED_COUNT_GENRES);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertGenre() {
        val expectedGenre = new Genre( "PL-SQL books");
        val actualGenre = dao.insert(expectedGenre);

        expectedGenre.setId(actualGenre.getId());

        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedGenreById() {
        val expectedGenre = new Genre(2, "Programming");
        val actualGenre = dao.getById(expectedGenre.getId());
        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateGenre() {
        var expectedGenre = new Genre(2, "Programming");
        var actualGenre = dao.getById(expectedGenre.getId());
        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);

        expectedGenre = new Genre(2, "Programming books");
        actualGenre = dao.update(expectedGenre);
        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Удалять запись из БД")
    @Test
    void shouldDeleteGenre() {
        assertThat(dao.deleteById(EXISTING_GENRE_ID)).isEqualTo(1);
        assertThatThrownBy(() -> dao.getById(EXISTING_GENRE_ID))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}