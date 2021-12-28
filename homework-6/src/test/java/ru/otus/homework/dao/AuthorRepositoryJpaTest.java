package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.homework.domain.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AuthorRepositoryJpa.class)
@DisplayName("Репозиторий для работы с авторами должен")
class AuthorRepositoryJpaTest {

    public static final int EXPECTED_COUNT_AUTHORS = 7;
    public static final long EXISTING_AUTHOR_ID = 7;

    @Autowired
    private AuthorRepositoryJpa repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedAuthorsCount() {
        val actualAuthorCount = repository.count();
        assertThat(actualAuthorCount).isEqualTo(EXPECTED_COUNT_AUTHORS);
    }

    @DisplayName("Возвращать все записи из БД")
    @Test
    void shouldReturnAllAuthors() {
        val actualAuthorsList = repository.getAll();
        assertThat(actualAuthorsList).isNotEmpty()
                .allMatch(a -> a.getId() > 0)
                .allMatch(a -> a.getName().length() > 0)
                .size().isEqualTo(EXPECTED_COUNT_AUTHORS);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertAuthor() {
        val newAuthor = new Author(0, "Лермонтов", "Михаил", "Юрьевич");
        val allAuthors = repository.getAll();
        assertThat(allAuthors).allMatch(a -> !a.getName().equals(newAuthor.getName()))
                .allMatch(a -> !a.getSurname().equals(newAuthor.getSurname()))
                .allMatch(a -> a.getPatronymic() == null || !a.getPatronymic().equals(newAuthor.getPatronymic()));

        val actualAuthor = repository.save(newAuthor);
        val expectedAuthor = em.find(Author.class, actualAuthor.getId());

        assertThat(actualAuthor)
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedAuthorById() {
        val optionalAuthor = repository.getById(EXISTING_AUTHOR_ID);
        val expectedAuthor = em.find(Author.class, EXISTING_AUTHOR_ID);

        assertThat(optionalAuthor).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateAuthor() {
        var optionalAuthor = repository.getById(EXISTING_AUTHOR_ID);
        assertThat(optionalAuthor).isPresent();

        val actualAuthor = optionalAuthor.get();
        actualAuthor.setName("Test name");
        repository.save(actualAuthor);

        val expectedAuthor = em.find(Author.class, EXISTING_AUTHOR_ID);

        assertThat(actualAuthor)
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @DisplayName("Удаляет запись из БД")
    @Test
    void shouldDeleteAuthor() {
        assertThat(repository.deleteById(EXISTING_AUTHOR_ID)).isEqualTo(1);
        assertThat(repository.getById(EXISTING_AUTHOR_ID).isEmpty());
    }

}