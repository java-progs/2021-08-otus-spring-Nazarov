package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Репозиторий для работы с книгами должен")
class BookRepositoryJpaTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertBook() {

        val authorsList = new ArrayList<Author>();
        val genresList = new ArrayList<Genre>();

        val newBook = new Book(0, "PL/SQL programming", null, authorsList, genresList);

        authorsList.add(new Author(0, "Author", "Test", null));
        genresList.add(new Genre(0, "Test"));

        val allBooks = repository.findAll();
        assertThat(allBooks).allMatch(b -> !b.getName().equals(newBook.getName()))
                .allMatch(a -> a.getIsbn() == null || !a.getIsbn().equals(newBook.getIsbn()));

        val actualBook = repository.save(newBook);
        val expectedBook = em.find(Book.class, actualBook.getId());

        assertThat(actualBook)
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedBookById() {
        val authorsList = new ArrayList<Author>();
        val genresList = new ArrayList<Genre>();

        val newBook = new Book(0, "PL/SQL programming", null, authorsList, genresList);

        authorsList.add(new Author(0, "Author", "Test", null));
        genresList.add(new Genre(0, "Test"));

        val allBooks = repository.findAll();
        assertThat(allBooks).allMatch(b -> !b.getName().equals(newBook.getName()))
                .allMatch(a -> a.getIsbn() == null || !a.getIsbn().equals(newBook.getIsbn()));

        em.persist(newBook);

        val optionalActualBook = repository.findById(newBook.getId());

        assertThat(optionalActualBook).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(newBook);
    }

}