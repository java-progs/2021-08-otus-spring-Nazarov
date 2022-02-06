package ru.otus.homework.service;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Сервис для работы с книгами должен ")
class BookServiceImplTest {

    private BookRepository bookRepository;
    private GenreRepository genreRepository;
    private AuthorRepository authorRepository;
    private BookService bookService;

    @BeforeEach
    void configureMocks() {
        bookRepository = mock(BookRepository.class);
        genreRepository = mock(GenreRepository.class);
        authorRepository = mock(AuthorRepository.class);

        bookService = new BookServiceImpl(bookRepository, authorRepository, genreRepository);
    }

    @DisplayName("возвращать корректное количество книг")
    @Test
    void shouldReturnCountBook() {
        val actualCount = 100L;
        given(bookRepository.count()).willReturn(actualCount);
        val expectedCount = bookService.getCountBooks();
        assertThat(expectedCount).isEqualTo(actualCount);
    }

    @DisplayName("возвращать список книг")
    @Test
    void shouldReturnAllBook() {
        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val authorSecond = new Author("2", "Sedgewick", "Robert", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val bookFirst = new Book("1", "Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);
        val bookSecond = new Book("2", "Algorithms", "",
                List.of(authorSecond), List.of(genreFirst, genreSecond), null);

        val actualBooksList = List.of(bookFirst, bookSecond);
        given(bookRepository.findAll()).willReturn(actualBooksList);
        val expectedBooksList = bookService.getAllBooks();

        assertThat(expectedBooksList).size().isEqualTo(actualBooksList.size());
        assertThat(expectedBooksList).containsExactlyInAnyOrderElementsOf(actualBooksList);
    }

    @DisplayName("возвращать список книг автора")
    @Test
    void shouldReturnAllBookByAuthor() {
        val authorSecond = new Author("2", "Sedgewick", "Robert", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val bookSecond = new Book("2", "Algorithms", "",
                List.of(authorSecond), List.of(genreFirst, genreSecond), null);

        val actualBooksList = List.of(bookSecond);
        given(bookRepository.findByAuthor(authorSecond.getId())).willReturn(actualBooksList);
        val expectedBooksList = bookService.getByAuthor(authorSecond.getId());

        assertThat(expectedBooksList).containsExactlyInAnyOrderElementsOf(actualBooksList);
    }

    @DisplayName("возвращать список книг жанра")
    @Test
    void shouldReturnAllBookByGenre() {
        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val authorSecond = new Author("2", "Sedgewick", "Robert", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val bookFirst = new Book("1", "Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);
        val bookSecond = new Book("2", "Algorithms", "",
                List.of(authorSecond), List.of(genreFirst, genreSecond), null);
        val actualBooksList = List.of(bookFirst, bookSecond);

        given(bookRepository.findByGenre(genreFirst.getId())).willReturn(actualBooksList);
        val expectedBooksList = bookService.getByGenre(genreFirst.getId());

        assertThat(expectedBooksList).containsExactlyInAnyOrderElementsOf(actualBooksList);
    }

    @DisplayName("возвращать книгу по id")
    @Test
    void shouldGetBookById() throws Exception {
        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val actualBook = new Book("1", "Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);

        given(bookRepository.findById(actualBook.getId())).willReturn(Optional.of(actualBook));
        val expectedBook = bookService.getBookById(actualBook.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("добавлять книгу")
    @Test
    void shouldSaveBook() {
        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val newBook = new Book("Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);
        val actualBook = new Book("100", "Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);

        given(bookRepository.save(newBook)).willReturn(actualBook);
        val expectedBook = bookService.saveBook(newBook);
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("обновлять книгу")
    @Test
    void shouldUpdateBook() {
        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val updatedBook = new Book("100","Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);
        given(bookRepository.save(updatedBook)).willReturn(updatedBook);
        val result = bookService.updateBook(updatedBook);
        assertThat(result).isTrue();
    }

    @DisplayName("удалять книгу")
    @Test
    void shouldDeleteBook() {
        val bookId = "100";
        doNothing().when(bookRepository).deleteById(bookId);
        bookService.deleteBookById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @DisplayName("возвращать все комментарии книги")
    @Test
    void shouldGetBookComments() {
        val commentFirst = new Comment("1", "User", LocalDateTime.now(), "Text");
        val commentSecond = new Comment("2", "reader", LocalDateTime.now(), "Comment text");
        val actualComments = List.of(commentFirst, commentSecond);
        val book = new Book("1", "", "", null, null, actualComments);
        given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));

        val expectedComments = bookService.getAllBookComments(book.getId());
        assertThat(expectedComments).containsExactlyInAnyOrderElementsOf(actualComments);
    }

    @DisplayName("возвращать комментарий к книге")
    @Test
    void shouldReturnBookCommentById() throws RecordNotFoundException {
        val commentFirst = new Comment("1", "User", LocalDateTime.now(), "Text");
        val commentSecond = new Comment("2", "reader", LocalDateTime.now(), "Comment text");
        val actualComments = List.of(commentFirst, commentSecond);
        val book = new Book("1", "", "", null, null, actualComments);
        given(bookRepository.findComment(book.getId(), commentSecond.getId()))
                .willReturn(commentSecond);

        val expectedComment = bookService.getComment(book.getId(), commentSecond.getId());
        assertThat(expectedComment).usingRecursiveComparison().isEqualTo(commentSecond);
    }

    @DisplayName("добавлять комментарий к книге")
    @Test
    void shouldAddComment() {
        val newComment = new Comment("User1", "text");
        given(bookRepository.addComment(eq("1"), any(Comment.class))).willReturn(true);

        assertThat(bookService.addComment("1", newComment)).isTrue();
        assertThat(newComment.getId()).isNotNull();
        assertThat(newComment.getTime()).isNotNull();

        verify(bookRepository, times(1)).addComment(eq("1"), any(Comment.class));
    }

    @DisplayName("обновлять комментарий")
    @Test
    void shouldUpdateComment() throws RecordNotFoundException {
        val comment = new Comment("1", "User1", LocalDateTime.now(), "text (updated)");
        val updatedComment = new Comment("1", "User1", comment.getTime(), "text (updated)");

        given(bookRepository.findComment("100", comment.getId())).willReturn(comment);
        given(bookRepository.updateComment(eq("100"), any(Comment.class))).willReturn(true);
        val result = bookService.updateComment("100", updatedComment);
        assertThat(result).isTrue();
        assertThat(updatedComment.getTime()).isNotNull().isNotEqualTo(comment.getTime());

        verify(bookRepository, times(1)).updateComment(eq("100"), any(Comment.class));
    }

    @DisplayName("удалять комментарий")
    @Test
    void shouldDeleteComment() {
        given(bookRepository.deleteComment("100", "1")).willReturn(true);
        assertThat(bookService.deleteComment("100", "1")).isTrue();

        verify(bookRepository, times(1)).deleteComment("100", "1");
    }

}