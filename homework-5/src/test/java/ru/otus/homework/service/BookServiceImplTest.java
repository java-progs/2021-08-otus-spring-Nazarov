package ru.otus.homework.service;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@DisplayName("Сервис для работы с книгами должен")
@SpringBootTest
class BookServiceImplTest {

    private final int EXPECTED_BOOK_COUNT = 3;
    private final int EXPECTED_BOOK_ID = 2;

    private final int AUTHOR_FOR_SEARCH_BOOK = 2;
    private final int EXPECTED_BOOK_COUNT_FOR_AUTHOR = 1;

    private final int GENRE_FOR_SEARCH_BOOK = 2;
    private final int EXPECTED_BOOK_COUNT_FOR_GENRE = 2;

    @Autowired
    private BookService bookService;

    @DisplayName("возвращать верное количество книг")
    @Test
    void shouldReturnCountBook() {
        val actualBooksCount = bookService.getCountBooks();
        assertThat(actualBooksCount).isEqualTo(EXPECTED_BOOK_COUNT);
    }

    @DisplayName("возвращать список всех книг")
    @Test
    void shouldReturnAllBook() {
        val actualBooksList = bookService.getAllBooks();
        assertThat(actualBooksList.size()).isEqualTo(EXPECTED_BOOK_COUNT);
    }

    @DisplayName("возвращать книгу с существующим id")
    @Test
    void shouldReturnBookById() {
        assertThatCode(() -> bookService.getBookById(EXPECTED_BOOK_ID))
                .doesNotThrowAnyException();
    }

    @DisplayName("возвращать исключение при поиске книги с несуществующим id")
    @Test
    void shouldNotReturnBookById() {
        assertThatThrownBy(() -> bookService.getBookById(EXPECTED_BOOK_ID + 100))
                .isInstanceOf(RecordNotFoundException.class);
    }

    @DisplayName("возвращать список книг автора")
    @Test
    void shouldReturnBooksListByAuthor() {
        val booksList = bookService.getBooksByAuthor(AUTHOR_FOR_SEARCH_BOOK);
        val booksCount = booksList.size();
        assertThat(bookService.getBooksByAuthor(AUTHOR_FOR_SEARCH_BOOK).size())
                .isEqualTo(EXPECTED_BOOK_COUNT_FOR_AUTHOR);
    }

    @DisplayName("возвращать пустой список книг для не существующего автора")
    @Test
    void shouldNotReturnBooksListByAuthor() {
        assertThat(bookService.getBooksByAuthor(10).size())
                .isEqualTo(0);
    }

    @DisplayName("возвращать список книг по жанру")
    @Test
    void shouldReturnBooksListByGenre() {
        assertThat(bookService.getBooksByGenre(GENRE_FOR_SEARCH_BOOK).size())
                .isEqualTo(EXPECTED_BOOK_COUNT_FOR_GENRE);
    }

    @DisplayName("возвращать пустой список книг для не существующего жанра")
    @Test
    void shouldNotReturnBooksListByGenre() {
        assertThat(bookService.getBooksByGenre(100).size())
                .isEqualTo(0);
    }

    @DisplayName("добавлять в базу книгу через объект")
    @Test
    void shouldAddedBookByObject() {
        val book = new Book("TestName", "100",
                List.of(new Author(1, "Pushkin", "Aleksandr", "Sergeevich")),
                List.of(new Genre(1, "Belletristic")));

        assertThat(bookService.addBook(book)).isTrue();
    }

    @DisplayName("добавлять в базу книгу через список полей")
    @Test
    void shouldAddedBookByFields() {
        assertThat(bookService.addBook("TestName", "100",
                new Long[] {3L, 6L},
                new Long[] {2L, 3L})).isTrue();
    }

    @DisplayName("не добавлять в базу книгу с несуществующим автором через список полей")
    @Test
    void shouldNotAddedBookByFieldsWrongAuthor() {
        assertThat(bookService.addBook("TestName", "100",
                new Long[] {10L, 3L},
                new Long[] {2L, 3L})).isFalse();
    }

    @DisplayName("не добавлять в базу книгу с несуществующим жанром через список полей")
    @Test
    void shouldNotAddedBookByFieldsWrongGenre() {
        assertThat(bookService.addBook("TestName", "100",
                new Long[] {3L},
                new Long[] {18L, 3L})).isFalse();
    }

    @DisplayName("обновить запись о книге через объект")
    @Test
    void shouldUpdatedBookByObject() {
        val book = new Book(1, "TestName", "100",
                List.of(new Author(1, "Pushkin", "Aleksandr", "Sergeevich")),
                List.of(new Genre(1, "Belletristic")));

        assertThat(bookService.updateBook(book)).isTrue();
    }

    @DisplayName("обновить запись о книге через список полей")
    @Test
    void shouldUpdatedBookByFields() {
        assertThat(bookService.updateBook(1, "TestName", "100",
                new Long[] {3L, 6L},
                new Long[] {2L, 3L})).isTrue();
    }

    @DisplayName("не обновить запись о книге с несуществующим автором через список полей")
    @Test
    void shouldNotUpdatedBookByFieldsWrongAuthor() {
        assertThat(bookService.updateBook(1, "TestName", "100",
                new Long[] {10L, 3L},
                new Long[] {2L, 3L})).isFalse();
    }

    @DisplayName("не обновить запись о книге с несуществующим автором через список полей")
    @Test
    void shouldNotUpdatedBookByFieldsWrongGenres() {
        assertThat(bookService.updateBook(1, "TestName", "100",
                new Long[] {3L},
                new Long[] {18L, 3L})).isFalse();
    }

    @DisplayName("удалить книгу в бд")
    @Test
    void shouldDelete() {
        assertThat(bookService.deleteBookById(EXPECTED_BOOK_ID)).isTrue();
    }

}