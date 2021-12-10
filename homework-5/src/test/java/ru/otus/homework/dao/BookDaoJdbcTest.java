package ru.otus.homework.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import({BookDaoJdbc.class, AuthorDaoJdbc.class, GenreDaoJdbc.class})
@DisplayName("DAO для работы с книгами должен")
class BookDaoJdbcTest {

    public static final int EXPECTED_COUNT_BOOKS = 3;
    public static final int EXISTING_BOOK_ID = 1;

    @Autowired
    private BookDaoJdbc dao;

    @DisplayName("Возвращать ожидаемое количество записей в БД")
    @Test
    void shouldReturnExpectedBookCount() {
        int actualBookCount = dao.count();
        assertThat(actualBookCount).isEqualTo(EXPECTED_COUNT_BOOKS);
    }

    @DisplayName("Добавлять запись в БД")
    @Test
    void shouldInsertBook() {
        val expectedBook = new Book("Oracle PL/SQL Programming", null,
                List.of(new Author(4, "Feuerstein", "Steven", null),
                        new Author(5, "Pribyl", "Bill", null)),
                List.of(new Genre(2, "Programming")));

        val actualBook = dao.insert(expectedBook);
        expectedBook.setId(actualBook.getId());

        assertThat(actualBook)
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Возвращать запись по ее id")
    @Test
    void shouldReturnExpectedBookById() {
        val expectedBook = new Book(2, "Spring boot 2", null,
                List.of(new Author(2, "Gutierrez", "Felipe", null)),
                List.of(new Genre(2, "Programming"), new Genre(3, "Java-book")));
        val actualBook = dao.getById(expectedBook.getId());
        assertThat(actualBook)
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Возвращает список книг автора")
    @Test
    void shouldReturnExpectedAuthorBooksList() {
        var book = new Book("Test", null,
                List.of(new Author(2, "Gutierrez", "Felipe", null)),
                List.of(new Genre(2, "Programming")));
        book = dao.insert(book);

        val expectedList = List.of(book,
                new Book(2, "Spring boot 2", null,
                        List.of(new Author(2, "Gutierrez", "Felipe", null)),
                        List.of(new Genre(2, "Programming"), new Genre(3, "Java-book"))
                ));
        val actualList = dao.getBooksByAuthor(2);
        assertThat(actualList).containsExactlyInAnyOrderElementsOf(expectedList);
    }

    @DisplayName("Возвращает список книг по жанру")
    @Test
    void shouldReturnExpectedGenreBooksList() {
        val expectedList = List.of(new Book(3, "Kotlin in action", null,
                List.of(new Author(3, "Isakova", "Svetlana", null)),
                List.of(new Genre(2, "Programming"))),
                new Book(2, "Spring boot 2", null,
                        List.of(new Author(2, "Gutierrez", "Felipe", null)),
                        List.of(new Genre(2, "Programming"), new Genre(3, "Java-book"))
                ));

        val actualList = dao.getBooksByGenre(2);
        assertThat(actualList).containsExactlyInAnyOrderElementsOf(expectedList);
    }

    @DisplayName("Обновлять запись в БД")
    @Test
    void shouldUpdateBook() {
        var expectedBook = new Book(3, "Kotlin in action", null,
                List.of(new Author(3, "Isakova", "Svetlana", null)
                ),
                List.of(new Genre(2, "Programming")
                )
        );

        var actualBook = dao.getById(expectedBook.getId());
        assertThat(actualBook)
                .usingRecursiveComparison().isEqualTo(expectedBook);


        expectedBook = new Book(3, "Kotlin in action", null,
                List.of(new Author(3, "Isakova", "Svetlana", null),
                        new Author(6, "Jemerov", "Dmitry", null)
                ),
                List.of(new Genre(2, "Programming"),
                        new Genre(3, "Java-book")
                )
        );

        actualBook = dao.update(expectedBook);
        assertThat(actualBook)
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Удаляет запись из БД")
    @Test
    void shouldDeleteGenre() {
        assertThat(dao.deleteById(EXISTING_BOOK_ID)).isEqualTo(1);
        assertThatThrownBy(() -> dao.getById(EXISTING_BOOK_ID))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}