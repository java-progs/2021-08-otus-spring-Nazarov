package ru.otus.homework.security;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.dto.BookDto;
import ru.otus.homework.dto.Mapper;
import ru.otus.homework.rest.BookController;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.BookService;
import ru.otus.homework.service.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Book controller должен ")
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private GenreService genreService;
    @MockBean
    private Mapper mapper;

    private final static String BOOK_ID = "100";
    private final static String AUTHOR_ID = "20";
    private final static String GENRE_ID = "1";

    @BeforeEach
    public void setupMocks() throws Exception {
        val author = new Author(AUTHOR_ID, "Pushkin", "Aleksandr");
        val genre = new Genre(GENRE_ID, "Test genre");
        val book = new Book("Book","", List.of(author), List.of(genre), null);
        when(mapper.toBook(any(BookDto.class))).thenReturn(book);
        when(authorService.getAuthorById(AUTHOR_ID)).thenReturn(author);
        when(authorService.getAllAuthors()).thenReturn(List.of(author));
        when(genreService.getGenreById(GENRE_ID)).thenReturn(genre);
        when(genreService.getAllGenres()).thenReturn(List.of(genre));
        when(bookService.getBookById(BOOK_ID)).thenReturn(book);
        when(bookService.getByAuthor(AUTHOR_ID)).thenReturn(List.of(book));
        when(bookService.getByGenre(GENRE_ID)).thenReturn(List.of(book));
    }

    @DisplayName("возвращать статус 302 на login для GET / неаутентифицированным пользователем")
    @Test
    public void getRootPageNoAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET / аутентифицированным пользователем")
    @Test
    public void getRootPageAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /books неаутентифицированным пользователем")
    @Test
    public void getAllAuthorsPageNoAuth() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /books аутентифицированным пользователем")
    @Test
    public void getAllAuthorsPageAuth() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /books?author= неаутентифицированным пользователем")
    @Test
    public void getBooksByAuthorPageNoAuth() throws Exception {
        mockMvc.perform(get("/books?author=" + AUTHOR_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /books?author= аутентифицированным пользователем")
    @Test
    public void getBooksByAuthorPageAuth() throws Exception {
        mockMvc.perform(get("/books?author=" + AUTHOR_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /books?genre= неаутентифицированным пользователем")
    @Test
    public void getBooksByGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/books?genre=" + GENRE_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /books?genre= аутентифицированным пользователем")
    @Test
    public void getBooksByGenrePageAuth() throws Exception {
        mockMvc.perform(get("/books?genre=" + GENRE_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /newBook неаутентифицированным пользователем")
    @Test
    public void getNewBookPageNoAuth() throws Exception {
        mockMvc.perform(get("/newBook"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /newBook аутентифицированным пользователем")
    @Test
    public void getNewBookPageAuth() throws Exception {
        mockMvc.perform(get("/newBook"))
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

    @WithMockUser(
            username = "user",
            roles = {"USER"}
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

    @DisplayName("возвращать статус 302 на login для GET /editBook неаутентифицированным пользователем")
    @Test
    public void getEditBookPageNoAuth() throws Exception {
        mockMvc.perform(get("/editBook?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /editBook аутентифицированным пользователем")
    @Test
    public void getEditBookPageAuth() throws Exception {
        mockMvc.perform(get("/editBook?id=" + BOOK_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /bookDetails неаутентифицированным пользователем")
    @Test
    public void getBookDetailsPageNoAuth() throws Exception {
        mockMvc.perform(get("/bookDetails?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /bookDetails аутентифицированным пользователем")
    @Test
    public void getBookDetailsPageAuth() throws Exception {
        mockMvc.perform(get("/bookDetails?id=" + BOOK_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /deleteBook неаутентифицированным пользователем")
    @Test
    public void getDeleteBookPageNoAuth() throws Exception {
        mockMvc.perform(get("/deleteBook?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /deleteBook аутентифицированным пользователем")
    @Test
    public void getDeleteBookPageAuth() throws Exception {
        mockMvc.perform(get("/deleteBook?id=" + BOOK_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /deleteBook неаутентифицированным пользователем")
    @Test
    public void postDeleteBookPageNoAuth() throws Exception {
        mockMvc.perform(post("/deleteBook?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 302 на books для POST /deleteBook аутентифицированным пользователем")
    @Test
    public void postDeleteBookPageAuth() throws Exception {
        mockMvc.perform(post("/deleteBook?id=" + BOOK_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/books"));
    }

}
