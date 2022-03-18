package ru.otus.homework.security;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.service.*;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Book controller должен ")
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BookService bookService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private GenreService genreService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private Mapper mapper;

    private final static long BOOK_ID = 100;
    private final static long AUTHOR_ID = 20;
    private final static long GENRE_ID = 1;

    @BeforeEach
    public void setupMocks() throws Exception {
        val author = new Author(AUTHOR_ID, "Pushkin", "Aleksandr", "");
        val genre = new Genre(GENRE_ID, "Test genre");
        val book = new Book(0L, "Book","", List.of(author), List.of(genre));
        when(mapper.toBook(any(BookDto.class))).thenReturn(book);
        when(authorService.getAuthorById(AUTHOR_ID)).thenReturn(author);
        when(authorService.getAllAuthors()).thenReturn(List.of(author));
        when(genreService.getGenreById(GENRE_ID)).thenReturn(genre);
        when(genreService.getAllGenres()).thenReturn(List.of(genre));
        when(bookService.getBookById(BOOK_ID)).thenReturn(book);
        when(bookService.getAllByAuthor(AUTHOR_ID)).thenReturn(List.of(book));
        when(bookService.getAllByGenre(GENRE_ID)).thenReturn(List.of(book));
    }

    @ParameterizedTest(name = "{index}: GET {0} вернет 302 на /login для неаутентифицированного пользователя")
    @ValueSource(strings = { "/", "/books", "/books?author=" + AUTHOR_ID, "/books?genre=" + GENRE_ID,
            "/newBook", "/editBook?id=" + BOOK_ID, "/bookDetails?id=" + BOOK_ID, "/deleteBook?id=" + BOOK_ID})
    public void noAuthUserGetPage302(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @ParameterizedTest(name = "{index}: GET {0} вернет 200 для аутентифицированного пользователя")
    @ValueSource(strings = { "/", "/books", "/books?author=" + AUTHOR_ID, "/books?genre=" + GENRE_ID,
            "/newBook", "/editBook?id=" + BOOK_ID, "/bookDetails?id=" + BOOK_ID, "/deleteBook?id=" + BOOK_ID})
    public void noAuthUserGetPage200(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /newBook неаутентифицированным пользователем")
    @Test
    public void postNewBookNoAuth() throws Exception {
        mockMvc.perform(post("/newBook")
                        .contentType("application/x-www-form-urlencoded")
                        .content("id=&name=&isbn=&authorsId=&genresId=&saveBook=pressed"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @DisplayName("возвращать статус 302 на login для POST /deleteBook неаутентифицированным пользователем")
    @Test
    public void postDeleteBookPageNoAuth() throws Exception {
        mockMvc.perform(post("/deleteBook?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"}
    )
    @DisplayName("возвращать статус 302 на books для POST /newBook аутентифицированным пользователем")
    @Test
    public void postNewBookAuth() throws Exception {
        mockMvc.perform(post("/newBook")
                        .contentType("application/x-www-form-urlencoded")
                        .content("id=&name=&isbn=&authorsId=&genresId=&saveBook=pressed"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/books"));
    }

    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"}
    )
    @DisplayName("возвращать статус 302 на books для POST /deleteBook аутентифицированным пользователем")
    @Test
    public void postDeleteBookPageAuth() throws Exception {
        mockMvc.perform(post("/deleteBook?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/books"));
    }

    @WithMockUser (
            username = "admin",
            roles = {"ADMIN"}
    )
    @ParameterizedTest(name = "{index}: Get {0} вернет 200 для пользователя с ролью ADMIN")
    @ValueSource(strings = { "/newBook", "/editBook?id=" + BOOK_ID, "/deleteBook?id=" + BOOK_ID })
    public void allowNewAuthorForAdmin(String path) throws Exception {
        val result = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result.contains("Access denied")).isFalse();
    }

    @WithMockUser (
            username = "user",
            roles = {"USER"}
    )
    @ParameterizedTest(name = "{index}: Get {0} вернет 200 c текстом Access denied для пользователя с ролью USER")
    @ValueSource(strings = { "/newBook", "/editBook?id=" + BOOK_ID, "/deleteBook?id=" + BOOK_ID })
    public void deniedNewAuthorForUser(String path) throws Exception {
        val result = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result.contains("Access denied")).isTrue();
    }
}
