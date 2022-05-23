package ru.otus.homework.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.rest.GenreController;
import ru.otus.homework.service.GenreService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Genre controller должен ")
@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService service;

    private final static String GENRE_ID = "100";

    @BeforeEach
    public void setupMocks() throws Exception {
        when(service.getGenreById(GENRE_ID)).thenReturn(new Genre(GENRE_ID, "Test"));
    }

    @DisplayName("возвращать статус 302 на login для GET /genres неаутентифицированным пользователем")
    @Test
    public void getAllGenresPageNoAuth() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /genres аутентифицированным пользователем")
    @Test
    public void getAllGenresPageAuth() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /addGenre неаутентифицированным пользователем")
    @Test
    public void getAddGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/addGenre"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /addGenre аутентифицированным пользователем")
    @Test
    public void getAddGenrePageAuth() throws Exception {
        mockMvc.perform(get("/addGenre"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /addGenre неаутентифицированным пользователем")
    @Test
    public void postAddGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/addGenre"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 302 на genres для POST /addGenre аутентифицированным пользователем")
    @Test
    public void postAddGenrePageAuth() throws Exception {
        mockMvc.perform(post("/addGenre"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/genres"));
    }

    @DisplayName("возвращать статус 302 на login для GET /editGenre неаутентифицированным пользователем")
    @Test
    public void getEditGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/editGenre?id=" + GENRE_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /editGenre аутентифицированным пользователем")
    @Test
    public void getEditGenrePageAuth() throws Exception {
        mockMvc.perform(get("/editGenre?id=" + GENRE_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /editGenre неаутентифицированным пользователем")
    @Test
    public void postEditGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/editGenre"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 302 на genres для POST /editGenre аутентифицированным пользователем")
    @Test
    public void postEditGenrePageAuth() throws Exception {
        mockMvc.perform(post("/editGenre"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/genres"));
    }

    @DisplayName("возвращать статус 302 на login для GET /deleteGenre неаутентифицированным пользователем")
    @Test
    public void getDeleteGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/deleteGenre?id=" + GENRE_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /deleteGenre аутентифицированным пользователем")
    @Test
    public void getDeleteGenrePageAuth() throws Exception {
        mockMvc.perform(get("/deleteGenre?id=" + GENRE_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /deleteGenre неаутентифицированным пользователем")
    @Test
    public void postDeleteGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/deleteGenre"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 302 на genres для POST /deleteGenre аутентифицированным пользователем")
    @Test
    public void postDeleteGenrePageAuth() throws Exception {
        mockMvc.perform(post("/deleteGenre?id=" + GENRE_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/genres"));
    }
}
