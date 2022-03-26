package ru.otus.homework.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.config.AppProps;
import ru.otus.homework.rest.LoginController;
import ru.otus.homework.service.CustomUserDetailsService;
import ru.otus.homework.service.UserServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Login controller должен ")
@WebMvcTest(LoginController.class)
@Import({AppProps.class})
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;
    @MockBean
    private UserServiceImpl userService;

    @DisplayName("возвращать статус 200 для неаутентифицированого пользователя")
    @Test
    public void testLoginPageAuth() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

}
