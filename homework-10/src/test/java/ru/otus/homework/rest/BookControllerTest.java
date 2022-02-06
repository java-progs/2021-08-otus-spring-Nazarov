package ru.otus.homework.rest;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.GenreRepository;
import ru.otus.homework.service.BookService;
import ru.otus.homework.util.TestUtil;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Book контроллер должен ")
class BookControllerTest {

    private BookService bookService;
    private MockMvc mvc;
    private Mapper mapper;

    private static final String API = "/api/books";
    private static final String EXISTING_BOOK_ID = "20";
    private static final String NO_EXISTING_BOOK_ID = "10";

    @BeforeEach
    void createMocks() throws Exception {
        val authorRepository = mock(AuthorRepository.class);
        val genreRepository = mock(GenreRepository.class);

        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val authorSecond = new Author("2", "Sedgewick", "Robert", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val bookFirst = new Book(EXISTING_BOOK_ID, "Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);
        val bookSecond = new Book(EXISTING_BOOK_ID + "1", "Algorithms", "",
                List.of(authorSecond), List.of(genreFirst, genreSecond), null);

        mapper = new Mapper(authorRepository, genreRepository);
        bookService = mock(BookService.class);
        mvc = MockMvcBuilders.standaloneSetup(new BookController(bookService, mapper)).build();

        when(authorRepository.findById("1")).thenReturn(Optional.of(authorFirst));
        when(authorRepository.findById("2")).thenReturn(Optional.of(authorSecond));

        when(genreRepository.findById("1")).thenReturn(Optional.of(genreFirst));
        when(genreRepository.findById("2")).thenReturn(Optional.of(genreSecond));

        when(bookService.getAllBooks()).thenReturn(List.of(bookFirst, bookSecond));
        when(bookService.getBookById(EXISTING_BOOK_ID)).thenReturn(bookFirst);
        when(bookService.getBookById(NO_EXISTING_BOOK_ID)).thenThrow(RecordNotFoundException.class);
    }

    @DisplayName("вернуть 200 и json со всеми книгами")
    @Test
    public void shouldReturnAllBooks() throws Exception {
        mvc.perform(get(API))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].authorsId", hasSize(1)))
                .andExpect(jsonPath("$[1].genresId", hasSize(2)));

        verify(bookService, times(1)).getAllBooks();
        verifyNoMoreInteractions(bookService);
    }


    @DisplayName("вернуть 200 и json с запрашиваемой книгой")
    @Test
    public void shouldReturn200AndBook() throws Exception {
        mvc.perform(get(API + "/" + EXISTING_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(EXISTING_BOOK_ID)))
                .andExpect(jsonPath("$.name", equalTo("Java concurrency in practice")));

        verify(bookService, times(1)).getBookById(EXISTING_BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при запросе несуществующей книги")
    @Test
    public void shouldReturn404() throws Exception {
        mvc.perform(get(API + "/" + NO_EXISTING_BOOK_ID))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(NO_EXISTING_BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 201, путь и json при добавлении книги")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book("Algorithms", "", List.of(author), List.of(genre), null);
        val savedBook = new Book("123", book.getName(), book.getIsbn(), book.getAuthorsList(), book.getGenresList(), null);

        given(bookService.saveBook(book)).willReturn(savedBook);

        mvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(mapper.toDto(book)))
            ).andExpect(status().is(201))
                .andExpect(redirectedUrl(API + "/123"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo("123")))
                .andExpect(jsonPath("$.name", equalTo(savedBook.getName())));

        verify(bookService, times(1)).saveBook(book);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 200 и json при обновлении книги")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book(EXISTING_BOOK_ID,"Algorithms", "", List.of(author), List.of(genre), null);
        given(bookService.updateBook(book)).willReturn(true);

        mvc.perform(put(API + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(mapper.toDto(book)))
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(book.getId())));

        verify(bookService, times(1)).getBookById(book.getId());
        verify(bookService, times(1)).updateBook(book);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при обновлении несуществующей книги")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book(NO_EXISTING_BOOK_ID,"Algorithms", "", List.of(author), List.of(genre), null);

        mvc.perform(put(API + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(mapper.toDto(book)))
        ).andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(book.getId());
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 500 при обновлении книги с несовпадающим id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() throws Exception {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book(EXISTING_BOOK_ID,"Algorithms", "", List.of(author), List.of(genre), null);

        mvc.perform(put(API + "/" + book.getId() + "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(mapper.toDto(book)))
        ).andExpect(status().isBadRequest());

        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 200 при удалении книги")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {
        doNothing().when(bookService).deleteBookById(EXISTING_BOOK_ID);

        mvc.perform(delete(API + "/" + EXISTING_BOOK_ID))
                .andExpect(status().isOk());

        verify(bookService, times(1)).deleteBookById(EXISTING_BOOK_ID);
        verifyNoMoreInteractions(bookService);
    }

}