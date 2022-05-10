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
import ru.otus.homework.domain.Book;
import ru.otus.homework.rest.CommentController;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.service.BookService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Comment controller должен ")
@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService service;

    private final String COMMENT_ID = "100";
    private final String BOOK_ID = "100";

    @BeforeEach
    public void setupMocks() throws Exception {
        when(service.getBookById(BOOK_ID)).thenReturn(new Book(BOOK_ID, "TestBook", "", null, null, null));
    }

    @DisplayName("возвращать статус 302 на login для POST /addComment неаутентифицированным пользователем")
    @Test
    public void postAddCommentPageNoAuth() throws Exception {
        mockMvc.perform(post("/addComment?bookId=" + BOOK_ID)
                .contentType("application/x-www-form-urlencoded")
                .content("name=reader&commentText=Comment"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 200 для POST /addComment аутентифицированным пользователем")
    @Test
    public void postAddCommentPageAuth() throws Exception {
        mockMvc.perform(post("/addComment?bookId=" + BOOK_ID)
                        .contentType("application/x-www-form-urlencoded")
                        .content("name=reader&commentText=Comment"))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для GET /updateComment неаутентифицированным пользователем")
    @Test
    public void getAddGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/updateComment?bookId=" + BOOK_ID + "&commentId=" + COMMENT_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /updateComment аутентифицированным пользователем")
    @Test
    public void getAddGenrePageAuth() throws Exception {
        mockMvc.perform(get("/updateComment?bookId=" + BOOK_ID + "&commentId=" + COMMENT_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("возвращать статус 302 на login для POST /updateComment неаутентифицированным пользователем")
    @Test
    public void postAddGenrePageNoAuth() throws Exception {
        mockMvc.perform(post("/updateComment?bookId=" + BOOK_ID + "&commentId=" + COMMENT_ID)
                        .contentType("application/x-www-form-urlencoded")
                        .content("commentText=Comment"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @DisplayName("возвращать статус 200 для POST /updateComment аутентифицированным пользователем")
    @Test
    public void postAddGenrePageAuth() throws Exception {
        mockMvc.perform(post("/updateComment?bookId=" + BOOK_ID + "&commentId=" + COMMENT_ID)
                        .contentType("application/x-www-form-urlencoded")
                        .content("commentText=Comment"))
                .andExpect(status().is(200));
    }

    @DisplayName("возвращать статус 302 на login для GET /deleteComment неаутентифицированным пользователем")
    @Test
    public void getEditGenrePageNoAuth() throws Exception {
        mockMvc.perform(get("/deleteComment?bookId=" + BOOK_ID + "&commentId=" + COMMENT_ID))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @DisplayName("возвращать статус 200 для GET /deleteComment аутентифицированным пользователем")
    @Test
    public void getEditGenrePageAuth() throws Exception {
        mockMvc.perform(get("/deleteComment?bookId=" + BOOK_ID + "&commentId=" + COMMENT_ID))
                .andExpect(status().isOk());
    }

}
