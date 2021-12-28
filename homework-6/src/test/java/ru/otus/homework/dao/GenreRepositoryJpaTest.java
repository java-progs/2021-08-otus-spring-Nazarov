package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.homework.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(GenreRepositoryJpa.class)
@DisplayName("Репозиторий для работы с жанрами должен")
class GenreRepositoryJpaTest {

    public static final int EXPECTED_COUNT_GENRES = 4;
    public static final long EXISTING_GENRE_ID = 4;

    @Autowired
    private GenreRepositoryJpa repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedGenresCount() {
        long actualGenresCount = repository.count();
        assertThat(actualGenresCount).isEqualTo(EXPECTED_COUNT_GENRES);
    }

    @DisplayName("Возвращать все записи из БД")
    @Test
    void shouldReturnAllGenres() {
        val actualAuthorsList = repository.getAll();
        assertThat(actualAuthorsList).isNotEmpty()
                .allMatch(a -> a.getId() > 0)
                .allMatch(a -> a.getName().length() > 0)
                .size().isEqualTo(EXPECTED_COUNT_GENRES);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertGenre() {
        val newGenre = new Genre(0, "Test genre");
        val allGenres = repository.getAll();
        assertThat(allGenres).allMatch(a -> !a.getName().equals(newGenre.getName()));

        val actualGenre = repository.save(newGenre);
        val expectedGenre = em.find(Genre.class, actualGenre.getId());

        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedGenreById() {
        val optionalGenre = repository.getById(EXISTING_GENRE_ID);
        val expectedGenre = em.find(Genre.class, EXISTING_GENRE_ID);

        assertThat(optionalGenre).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateGenre() {
        var optionalGenre = repository.getById(EXISTING_GENRE_ID);
        assertThat(optionalGenre).isPresent();

        val actualGenre = optionalGenre.get();
        actualGenre.setName("Test name");
        repository.save(actualGenre);

        val expectedGenre = em.find(Genre.class, EXISTING_GENRE_ID);

        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Удаляет запись из БД")
    @Test
    void shouldDeleteGenre() {
        assertThat(repository.deleteById(EXISTING_GENRE_ID)).isEqualTo(1);
        assertThat(repository.getById(EXISTING_GENRE_ID).isEmpty());
    }

}