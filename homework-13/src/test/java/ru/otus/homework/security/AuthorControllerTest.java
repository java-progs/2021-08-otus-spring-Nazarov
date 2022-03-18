package ru.otus.homework.security;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Author;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.CustomUserDetailsService;
import ru.otus.homework.service.UserServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Author controller должен ")
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private AuthorService service;

    private final static long AUTHOR_ID = 100;

    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    protected class Config {

    }

    @BeforeEach
    public void setupMocks() throws Exception {
        when(service.getAuthorById(AUTHOR_ID)).thenReturn(new Author(AUTHOR_ID, "Pushkin", "Aleksandr", ""));
    }

    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"}
    )
    @ParameterizedTest(name = "{index}: GET {0} вернет 200 для аутентифицированного пользователя")
    @ValueSource(strings = { "/authors", "/addAuthor", "/editAuthor?id=" + AUTHOR_ID })
    public void authUserGetPage200(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{index}: GET {0} вернет 302 на /login для неаутентифицированного пользователя")
    @ValueSource(strings = { "/authors", "/addAuthor", "/editAuthor?id=" + AUTHOR_ID, "/deleteAuthor?id=" + AUTHOR_ID })
    public void noAuthUserGetPage302(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @ParameterizedTest(name = "{index}: GET {0} вернет 302 на /login для неаутентифицированного пользователя")
    @ValueSource(strings = { "/addAuthor", "/editAuthor", "/deleteAuthor" })
    public void noAuthUserPost302(String path) throws Exception {
        mockMvc.perform(post(path))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"}
    )
    @ParameterizedTest(name = "{index}: POST {0} вернет 302 на /authors для аутентифицированного пользователя")
    @ValueSource(strings = { "/addAuthor", "/editAuthor", "/deleteAuthor?id=" + AUTHOR_ID })
    public void authUserPost302(String path) throws Exception {
        mockMvc.perform(post(path))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/authors"));
    }

    @WithMockUser (
            username = "admin",
            roles = {"ADMIN"}
    )
    @ParameterizedTest(name = "{index}: Get {0} вернет 200 для пользователя с ролью ADMIN")
    @ValueSource(strings = { "/addAuthor", "/editAuthor?id=" + AUTHOR_ID, "/deleteAuthor?id=" + AUTHOR_ID })
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
    @ValueSource(strings = { "/addAuthor", "/editAuthor?id=" + AUTHOR_ID, "/deleteAuthor?id=" + AUTHOR_ID })
    public void deniedNewAuthorForUser(String path) throws Exception {
        val result = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result.contains("Access denied")).isTrue();
    }
}
