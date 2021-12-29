package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.homework.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Репозиторий для работы с жанрами должен")
class GenreRepositoryJpaTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertGenre() {
        val newGenre = new Genre(0, "Test genre");
        val allGenres = repository.findAll();
        assertThat(allGenres).allMatch(a -> !a.getName().equals(newGenre.getName()));

        val actualGenre = repository.save(newGenre);
        val expectedGenre = em.find(Genre.class, actualGenre.getId());

        assertThat(actualGenre)
                .usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedGenreById() {
        val newGenre = new Genre(0, "Test genre");
        val allGenres = repository.findAll();
        assertThat(allGenres).allMatch(a -> !a.getName().equals(newGenre.getName()));

        em.persist(newGenre);

        val optionalActualGenre = repository.findById(newGenre.getId());

        assertThat(optionalActualGenre).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(newGenre);
    }

}