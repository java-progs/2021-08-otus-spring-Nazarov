package ru.otus.homework.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Author;
import ru.otus.homework.rest.AuthorController;
import ru.otus.homework.service.AuthorService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Author controller должен ")
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService service;

    private final static String AUTHOR_ID = "100";

    @BeforeEach
    public void setupMocks() throws Exception {
        when(service.getAuthorById(AUTHOR_ID)).thenReturn(new Author(AUTHOR_ID, "Aleksandr", "Pushkin"));
    }

    @DisplayName("возвращать статус 302 на login для GET /authors неаутентифицированным пользователем")
    @Test
    public void getAllAuthorsPageNoAuth() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /authors аутентифицированным пользователем")
    @Test
    public void getAllAuthorsPageAuth() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /addAuthor неаутентифицированным пользователем")
    @Test
    public void getAddGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/addAuthor"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /addAuthor аутентифицированным пользователем")
    @Test
    public void getAddGenrePageAuth() throws Exception {
        mockMvc.perform(get("/addAuthor"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /addAuthor неаутентифицированным пользователем")
    @Test
    public void postAddGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/addAuthor"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 302 на authors для POST /addAuthor аутентифицированным пользователем")
    @Test
    public void postAddGenrePageAuth() throws Exception {
        mockMvc.perform(post("/addAuthor"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/authors"));
    }

    @DisplayName("возвращать статус 302 на login для GET /editAuthor неаутентифицированным пользователем")
    @Test
    public void getEditGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/editAuthors?id=" + AUTHOR_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /editAuthor аутентифицированным пользователем")
    @Test
    public void getEditGenrePageAuth() throws Exception {
        mockMvc.perform(get("/editAuthor?id=" + AUTHOR_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /editAuthor неаутентифицированным пользователем")
    @Test
    public void postEditGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/editAuthor"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 302 на authors для POST /editAuthor аутентифицированным пользователем")
    @Test
    public void postEditGenrePageAuth() throws Exception {
        mockMvc.perform(post("/editAuthor"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/authors"));
    }

    @DisplayName("возвращать статус 302 на login для GET /deleteAuthor неаутентифицированным пользователем")
    @Test
    public void getDeleteGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/deleteAuthor?id=" + AUTHOR_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /deleteAuthor аутентифицированным пользователем")
    @Test
    public void getDeleteGenrePageAuth() throws Exception {
        mockMvc.perform(get("/deleteAuthor?id=" + AUTHOR_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /deleteAuthor неаутентифицированным пользователем")
    @Test
    public void postDeleteGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/deleteAuthor"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 302 на Authors для POST /deleteAuthor аутентифицированным пользователем")
    @Test
    public void postDeleteGenrePageAuth() throws Exception {
        mockMvc.perform(post("/deleteAuthor?id=" + AUTHOR_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/authors"));
    }
}
