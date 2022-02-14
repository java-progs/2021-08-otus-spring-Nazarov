package ru.otus.homework.rest;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.ObjectNotFoundException;
import ru.otus.homework.service.BookService;
import ru.otus.homework.util.TestUtil;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({CommentController.class})
@WebMvcTest(CommentController.class)
@DisplayName("Comment контроллер должен ")
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    private static final String API = "/api/books/bookId/comments";
    private static final String EXISTING_BOOK_ID = "700";
    private static final String NO_EXISTING_BOOK_ID = "800";

    @BeforeEach
    public void bookServicePrepare() throws Exception {
        given(bookService.existById(EXISTING_BOOK_ID)).willReturn(true);
        given(bookService.existById(NO_EXISTING_BOOK_ID)).willReturn(false);
    }

    private String getApiPath(String bookId) {
        return API.replaceFirst("bookId", bookId);
    }

    @DisplayName("вернуть 200 и json со всеми комментариями к книге")
    @Test
    public void shouldReturnAllComments() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val commentFirst = new Comment("1", "User1", "Text comment");
        val commentSecond = new Comment("2", "book reader", "This is an interesting book");
        val commentsList = List.of(commentFirst, commentSecond);
        given(bookService.getAllBookComments(bookId)).willReturn(commentsList);
        mvc.perform(get(getApiPath(bookId)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).getAllBookComments(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 200 и пустой json при отсутствии комментариев")
    @Test
    public void shouldReturnEmptyCommentsList() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        given(bookService.getAllBookComments(bookId)).willReturn(null);
        mvc.perform(get(getApiPath(bookId)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).getAllBookComments(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при запросе комментариев к несуществующей книге")
    @Test
    public void shouldReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        mvc.perform(get(getApiPath(bookId)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).existById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 200 и json с запрашиваемым комментарием")
    @Test
    public void shouldReturn200AndComment() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("2", "reader", LocalDateTime.now(), "text");

        given(bookService.getComment(bookId, comment.getId())).willReturn(comment);
        mvc.perform(get(getApiPath(bookId) + "/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(comment.getId())))
                .andExpect(jsonPath("$.text", equalTo(comment.getText())));

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).getComment(bookId, comment.getId());
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при запросе несуществующего комментария")
    @Test
    public void shouldReturn404CommentNotFound() throws Exception {
        val bookId = EXISTING_BOOK_ID;

        given(bookService.getComment(bookId, "2")).willThrow(ObjectNotFoundException.class);
        mvc.perform(get(getApiPath(bookId) + "/2"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).getComment(bookId, "2");
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при запросе комментария к несуществующей книге")
    @Test
    public void shouldReturn404BookNotFound() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;

        mvc.perform(get(getApiPath(bookId) + "/2"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).existById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 201, путь и json при добавлении комментария к книге")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("Reader", "text comment");
        val commentId = "21";
        val commentTime = LocalDateTime.now();
        val resultComment = new Comment(commentId, comment.getAuthor(), commentTime, comment.getText());
        when(bookService.addComment(bookId, comment)).then(invocation -> {
                    var savedComment = (Comment) invocation.getArgument(1);
                    savedComment.setId(commentId);
                    savedComment.setTime(commentTime);
                    return true;
                }
            );

        mvc.perform(post(getApiPath(bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(comment))
            ).andExpect(status().is(201))
                .andExpect(redirectedUrl(getApiPath(bookId) + "/21"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo("21")))
                .andExpect(jsonPath("$.text", equalTo("text comment")));

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).addComment(bookId, resultComment);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при добавлении комментария к несуществующей книге")
    @Test
    public void shouldNotAddAndReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        val comment = new Comment("Reader", "textComment");

        mvc.perform(post(getApiPath(bookId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.getJsonBytes(comment))
                ).andExpect(status().isNotFound());

        verify(bookService, times(1)).existById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 200 и json при обновлении комментария")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("21", "User1", null, "text comment for update");
        val timeUpdate = LocalDateTime.now();
        val resultComment = new Comment(comment.getId(), comment.getAuthor(), timeUpdate, comment.getText());

        when(bookService.updateComment(bookId, comment))
                .then(invocation -> {
                    val updatedComment = (Comment) invocation.getArgument(1);
                    updatedComment.setTime(timeUpdate);
                    return true;
                });

        mvc.perform(put(getApiPath(bookId) + "/" + comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(comment))
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(comment.getId())))
                .andExpect(jsonPath("$.time", not(comment.getTime())));

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).updateComment(bookId, resultComment);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при обновлении комментария несуществующей книги")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        val comment = new Comment("21", "User1", LocalDateTime.now(), "text comment for update");

        mvc.perform(put(getApiPath(bookId) + "/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.getJsonBytes(comment))
                ).andExpect(status().isNotFound());

        verify(bookService, times(1)).existById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 500 при обновлении комментария с несовпадающем id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("21", "User1", LocalDateTime.now(), "text comment for update");

        mvc.perform(put(getApiPath(bookId) + "/" + comment.getId() + "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(comment))
        ).andExpect(status().isBadRequest());

        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 200 при удалении комментария")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        given(bookService.deleteComment(bookId, "21")).willReturn(true);

        mvc.perform(delete(getApiPath(bookId) + "/21"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).existById(bookId);
        verify(bookService, times(1)).deleteComment(bookId, "21");
        verifyNoMoreInteractions(bookService);
    }

    @DisplayName("вернуть 404 при удалении комментария несуществующей книги")
    @Test
    public void shouldNotDeleteAndReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;

        mvc.perform(delete(getApiPath(bookId) + "/21"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).existById(bookId);
        verifyNoMoreInteractions(bookService);
    }
}