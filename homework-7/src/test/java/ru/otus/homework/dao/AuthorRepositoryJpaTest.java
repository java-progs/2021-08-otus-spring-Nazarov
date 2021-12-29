package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Репозиторий для работы с авторами должен")
class AuthorRepositoryJpaTest {

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertAuthor() {
        val newAuthor = new Author(0, "Лермонтов", "Михаил", "Юрьевич");
        val allAuthors = repository.findAll();
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
        val newAuthor = new Author(0, "Лермонтов", "Михаил", "Юрьевич");
        val allAuthors = repository.findAll();
        assertThat(allAuthors).allMatch(a -> !a.getName().equals(newAuthor.getName()))
                .allMatch(a -> !a.getSurname().equals(newAuthor.getSurname()))
                .allMatch(a -> a.getPatronymic() == null || !a.getPatronymic().equals(newAuthor.getPatronymic()));

        em.persist(newAuthor);

        val optionalActualAuthor = repository.findById(newAuthor.getId());
        assertThat(optionalActualAuthor).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(newAuthor);
    }

}