package ru.otus.homework.rest;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest
@DisplayName("Book роутер должен ")
class BookRouterTest {

    private static final String EXISTING_BOOK_ID = "100";
    private static final String NO_EXISTING_BOOK_ID = "200";

    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private GenreRepository genreRepository;

    private WebTestClient client;
    private Mapper mapper;

    private static final String API = "/api/books";
    private List<Book> booksList;

    @BeforeEach
    void createMocks() {
        mapper = new Mapper(authorRepository, genreRepository);
        val router = new BookRouter();
        val routerFunction = router.composedBookRoutes(bookRepository, mapper);
        client = WebTestClient.bindToRouterFunction(routerFunction).build();

        val authorFirst = new Author("1", "Goetz", "Brian", "");
        val authorSecond = new Author("2", "Sedgewick", "Robert", "");
        val genreFirst = new Genre("1", "Programming");
        val genreSecond = new Genre("2", "Java");
        val bookFirst = new Book(EXISTING_BOOK_ID, "Java concurrency in practice", "",
                List.of(authorFirst), List.of(genreFirst, genreSecond), null);
        val bookSecond = new Book(EXISTING_BOOK_ID + "1", "Algorithms", "",
                List.of(authorSecond), List.of(genreFirst, genreSecond), null);
        booksList = List.of(bookFirst, bookSecond);

        when(authorRepository.findById("1")).thenReturn(Mono.just(authorFirst));
        when(authorRepository.findById("2")).thenReturn(Mono.just(authorSecond));

        when(genreRepository.findById("1")).thenReturn(Mono.just(genreFirst));
        when(genreRepository.findById("2")).thenReturn(Mono.just(genreSecond));

        when(bookRepository.findAll()).thenReturn(Flux.just(bookFirst, bookSecond));
        when(bookRepository.findById(EXISTING_BOOK_ID)).thenReturn(Mono.just(bookFirst));
        when(bookRepository.findById(NO_EXISTING_BOOK_ID)).thenReturn(Mono.empty());
    }

    @DisplayName("вернуть 200 и json со всеми книгами")
    @Test
    public void shouldReturnAllBooks() {
        client.get()
                .uri(API)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class)
                .hasSize(booksList.size())
                .value(response -> response.containsAll(booksList));

        verify(bookRepository, times(1)).findAll();
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 и json с запрашиваемой книгой")
    @Test
    public void shouldReturn200AndBook() throws Exception {
        client.get()
                .uri(API + '/' + EXISTING_BOOK_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .value(response -> response.equals(booksList.get(0)));

        verify(bookRepository, times(1)).findById(EXISTING_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 404 при запросе несуществующей книги")
    @Test
    public void shouldReturn404() throws Exception {
        client.get()
                .uri(API + '/' + NO_EXISTING_BOOK_ID)
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository, times(1)).findById(NO_EXISTING_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 201, путь и json при добавлении книги")
    @Test
    public void shouldAddAndReturn201() {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book("Algorithms", "", List.of(author), List.of(genre), null);
        val savedBook = new Book("123", book.getName(), book.getIsbn(), book.getAuthorsList(), book.getGenresList(), null);
        when(bookRepository.save(book)).thenReturn(Mono.just(savedBook));

        client.post()
                .uri(API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mapper.toDto(book)), BookDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .location(API + "/" + savedBook.getId())
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedBook.getId())
                .jsonPath("$.name").isEqualTo(savedBook.getName())
                .jsonPath("$.isbn").isEqualTo(savedBook.getIsbn())
                .jsonPath("$.authorsId[0]").isEqualTo(author.getId())
                .jsonPath("$.genresId[0]").isEqualTo(genre.getId());

        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 и json при обновлении книги")
    @Test
    public void shouldUpdateAndReturn200() {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book(EXISTING_BOOK_ID,"Algorithms", "", List.of(author), List.of(genre), null);
        when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        when(bookRepository.save(book)).thenReturn(Mono.just(book));

        client.put()
                .uri(API + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mapper.toDto(book)), BookDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(response -> response.equals(book));

        verify(bookRepository, times(1)).existsById(book.getId());
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 404 при обновлении несуществующей книги")
    @Test
    public void shouldNotUpdateAndReturn404() {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book(NO_EXISTING_BOOK_ID,"Algorithms", "", List.of(author), List.of(genre), null);

        when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(false));

        client.put()
                .uri(API + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mapper.toDto(book)), BookDto.class)
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository, times(1)).existsById(book.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 500 при обновлении книги с несовпадающим id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() {
        val author = new Author("2", "Sedgewick", "Robert", "");
        val genre = new Genre("1", "Programming");
        val book = new Book(NO_EXISTING_BOOK_ID,"Algorithms", "", List.of(author), List.of(genre), null);

        client.put()
                .uri(API + "/" + book.getId() + "100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mapper.toDto(book)), BookDto.class)
                .exchange()
                .expectStatus().isNotFound();

        verifyNoInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 при удалении книги")
    @Test
    public void shouldDeleteAndReturn200() {
        when(bookRepository.deleteById(EXISTING_BOOK_ID)).thenReturn(Mono.empty());

        client.delete()
                .uri(API + "/" + EXISTING_BOOK_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(bookRepository, times(1)).deleteById(EXISTING_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

}