package ru.otus.homework.service;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.AuthorRepository;
import ru.otus.homework.dao.BookRepository;
import ru.otus.homework.dao.GenreRepository;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@DisplayName("Сервис для работы с книгами должен")
@SpringBootTest
class BookServiceImplTest {

    private static final int EXPECTED_BOOK_COUNT = 2;
    private static final long EXPECTED_BOOK_ID = 2;
    private static final List<Long> EXISTING_AUTHORS_IDS = List.of( 1L, 2L, 3L, 4L, 5L, 6L );
    private static final List<Long> EXISTING_GENRES_IDS = List.of( 1L, 2l, 3L, 4L, 5L );

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private GenreRepository genreRepository;
    private BookService bookService;

    @BeforeEach
    void createBookService() {
        bookRepository = mock(BookRepository.class);
        authorRepository = mock(AuthorRepository.class);
        genreRepository = mock(GenreRepository.class);
        bookService = new BookServiceImpl(bookRepository, authorRepository, genreRepository);

        val booksList = new ArrayList<Book>();

        for (int i = 1; i <= EXPECTED_BOOK_COUNT; i++) {
            booksList.add(new Book());
        }

        given(bookRepository.findAll()).willReturn(booksList);
        given(bookRepository.findById(EXPECTED_BOOK_ID)).willReturn(Optional.ofNullable(new Book()));
        doNothing().when(bookRepository).deleteById(anyLong());

        when(authorRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        when(authorRepository.findById(argThat(arg -> EXISTING_AUTHORS_IDS.contains(arg)))).thenReturn(Optional.of(new Author()));

        when(genreRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        when(genreRepository.findById(argThat(i -> EXISTING_GENRES_IDS.contains(i)))).thenReturn(Optional.of(new Genre()));

        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        given(bookRepository.count()).willReturn((long) EXPECTED_BOOK_COUNT);
    }

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

    @DisplayName("добавлять в базу книгу через объект")
    @Test
    void shouldAddedBookByObject() {
        val book = new Book(0, "TestName", "100",
                List.of(new Author(1, "Pushkin", "Aleksandr", "Sergeevich")),
                List.of(new Genre(1, "Belletristic")));

        assertThat(bookService.saveBook(book)).isTrue();
    }

    @DisplayName("добавлять в базу книгу через список полей")
    @Test
    void shouldAddedBookByFields() {
        assertThat(bookService.saveBook("TestName", "100",
                new Long[] {EXISTING_AUTHORS_IDS.get(1), EXISTING_AUTHORS_IDS.get(2)},
                new Long[] {EXISTING_GENRES_IDS.get(1)})).isTrue();
    }

    @DisplayName("не добавлять в базу книгу с несуществующим автором через список полей")
    @Test
    void shouldNotAddedBookByFieldsWrongAuthor() {
        assertThat(bookService.saveBook("TestName", "100",
                new Long[] {10L},
                new Long[] {EXISTING_GENRES_IDS.get(2), EXISTING_GENRES_IDS.get(3)})).isFalse();
    }

    @DisplayName("не добавлять в базу книгу с несуществующим жанром через список полей")
    @Test
    void shouldNotAddedBookByFieldsWrongGenre() {
        assertThat(bookService.saveBook("TestName", "100",
                new Long[] {EXISTING_AUTHORS_IDS.get(2), EXISTING_AUTHORS_IDS.get(3)},
                new Long[] {18L})).isFalse();
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
                new Long[] {EXISTING_AUTHORS_IDS.get(2), EXISTING_AUTHORS_IDS.get(3)},
                new Long[] {EXISTING_GENRES_IDS.get(2), EXISTING_GENRES_IDS.get(3)})).isTrue();
    }

    @DisplayName("не обновить запись о книге с несуществующим автором через список полей")
    @Test
    void shouldNotUpdatedBookByFieldsWrongAuthor() {
        assertThat(bookService.updateBook(1, "TestName", "100",
                new Long[] {EXISTING_AUTHORS_IDS.get(2), EXISTING_AUTHORS_IDS.get(3)},
                new Long[] {18L})).isFalse();
    }

    @DisplayName("не обновить запись о книге с несуществующим автором через список полей")
    @Test
    void shouldNotUpdatedBookByFieldsWrongGenres() {
        assertThat(bookService.updateBook(1, "TestName", "100",
                new Long[] {10L},
                new Long[] {EXISTING_GENRES_IDS.get(3), EXISTING_GENRES_IDS.get(4)})).isFalse();
    }

    @DisplayName("удалить книгу в бд")
    @Test
    void shouldDelete() {
        assertThatCode(() -> bookService.getBookById(EXPECTED_BOOK_ID)).doesNotThrowAnyException();
        assertThatCode(() -> bookService.deleteBookById(EXPECTED_BOOK_ID)).doesNotThrowAnyException();

        when(bookRepository.findById(EXPECTED_BOOK_ID)).thenReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> bookService.getBookById(EXPECTED_BOOK_ID)).isInstanceOf(RecordNotFoundException.class);
    }

}