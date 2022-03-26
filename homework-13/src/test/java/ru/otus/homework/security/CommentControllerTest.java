package ru.otus.homework.security;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.service.BookService;
import ru.otus.homework.service.CommentService;
import ru.otus.homework.service.CustomUserDetailsService;
import ru.otus.homework.service.UserServiceImpl;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Comment controller должен ")
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private BookService bookService;

    private final long COMMENT_ID = 100;
    private final long BOOK_ID = 100;

    @BeforeEach
    public void setupMocks() throws Exception {
        val book = new Book(0L, "Book","", List.of(), List.of());
        when(bookService.getBookById(anyLong())).thenReturn(book);
        when(commentService.getCommentById(COMMENT_ID)).thenReturn(
                new Comment(COMMENT_ID, "Test author", new Timestamp(System.currentTimeMillis()), "Comment text", book));
    }

    @ParameterizedTest(name = "{index}: POST {0} вернет 302 на /login для неаутентифицированного пользователя")
    @CsvSource( value = {
            "/addComment?bookId=100, name=reader&commentText=Comment",
            "/updateComment?bookId=100&commentId=100, commentText=Comment"
    })
    public void postNoAuth(String path, String content) throws Exception {
        mockMvc.perform(post(path)
                        .contentType("application/x-www-form-urlencoded")
                        .content(content))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    @ParameterizedTest(name = "{index}: POST {0} вернет 200 для аутентифицированного пользователя")
    @CsvSource( value = {
            "/addComment?bookId=100, name=reader&commentText=Comment",
            "/updateComment?bookId=100&commentId=100, commentText=Comment"
    })
    public void postAuth(String path, String content) throws Exception {
        mockMvc.perform(post(path)
                        .contentType("application/x-www-form-urlencoded")
                        .content(content))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{index}: GET {0} вернет 302 на /login для неаутентифицированного пользователя")
    @ValueSource(strings = {
            "/updateComment?bookId=100&commentId=100",
            "/deleteComment?bookId=100&commentId=100"
    })
    public void getNoAuth(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @WithMockUser(
            username = "user"
    )
    @ParameterizedTest(name = "{index}: GET {0} вернет 200 для аутентифицированного пользователя")
    @ValueSource(strings = {
            "/updateComment?bookId=100&commentId=100",
            "/deleteComment?bookId=100&commentId=100"
    })
    public void getAuth(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk());
    }

}
