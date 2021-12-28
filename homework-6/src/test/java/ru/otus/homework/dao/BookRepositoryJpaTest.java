package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BookRepositoryJpa.class, AuthorRepositoryJpa.class, GenreRepositoryJpa.class})
@DisplayName("Репозиторий для работы с книгами должен")
class BookRepositoryJpaTest {

    public static final int EXPECTED_COUNT_BOOKS = 3;
    public static final long EXISTING_BOOK_ID = 3;

    @Autowired
    private BookRepositoryJpa repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedBooksCount() {
        val actualAuthorCount = repository.count();
        assertThat(actualAuthorCount).isEqualTo(EXPECTED_COUNT_BOOKS);
    }

    @DisplayName("Возвращать все записи из БД")
    @Test
    void shouldReturnAllBooks() {
        val actualAuthorsList = repository.getAll();
        assertThat(actualAuthorsList).isNotEmpty()
                .allMatch(a -> a.getId() > 0)
                .allMatch(a -> a.getName().length() > 0)
                .allMatch(a -> a.getAuthorsList().size() > 0)
                .allMatch(a -> a.getGenresList().size() > 0)
                .size().isEqualTo(EXPECTED_COUNT_BOOKS);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertBook() {

        val authorsList = new ArrayList<Author>();
        val genresList = new ArrayList<Genre>();
        val newBook = new Book(0, "PL/SQL programming", null, authorsList, genresList);
        authorsList.add(new Author(0, "Author", "Test", null));
        genresList.add(new Genre(0, "Test"));

        val allBooks = repository.getAll();
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
        val optionalBook = repository.getById(EXISTING_BOOK_ID);
        val expectedBook = em.find(Book.class, EXISTING_BOOK_ID);

        assertThat(optionalBook).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateBook() {
        var optionalBook = repository.getById(EXISTING_BOOK_ID);
        assertThat(optionalBook).isPresent();

        val actualBook = optionalBook.get();
        val genresList = actualBook.getGenresList();
        genresList.add(new Genre(0, "Genre for book"));
        actualBook.setName("Test book");
        repository.save(actualBook);

        val expectedBook = em.find(Book.class, EXISTING_BOOK_ID);

        assertThat(actualBook)
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Удаляет запись из БД")
    @Test
    void shouldDeleteBook() {
        assertThat(repository.deleteById(EXISTING_BOOK_ID)).isEqualTo(1);
        assertThat(repository.getById(EXISTING_BOOK_ID).isEmpty());
    }

}