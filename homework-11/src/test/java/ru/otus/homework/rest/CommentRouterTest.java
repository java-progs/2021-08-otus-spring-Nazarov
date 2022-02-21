package ru.otus.homework.rest;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.repositories.BookRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@Import({CommentRouter.class})
@WebFluxTest(CommentRouter.class)
@DisplayName("Comment роутер должен ")
class CommentRouterTest {

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private WebTestClient client;

    private static final String API = "/api/books/bookId/comments";
    private static final String EXISTING_BOOK_ID = "700";
    private static final String NO_EXISTING_BOOK_ID = "800";

    @BeforeEach
    public void bookServicePrepare() {
        when(bookRepository.findById(EXISTING_BOOK_ID)).thenReturn(Mono.just(new Book()));
        when(bookRepository.findById(NO_EXISTING_BOOK_ID)).thenReturn(Mono.empty());
    }

    private String getApiPath(String bookId) {
        return API.replaceFirst("bookId", bookId);
    }

    @DisplayName("вернуть 200 и json со всеми комментариями к книге")
    @Test
    public void shouldReturnAllComments() throws Exception {
        val commentOne = new Comment("1", "User1", "Text comment");
        val commentSecond = new Comment("2", "book reader", "This is an interesting book");
        val commentsList = List.of(commentOne, commentSecond);
        val book = new Book(EXISTING_BOOK_ID, "","", null, null, commentsList);

        when(bookRepository.findById(book.getId())).thenReturn(Mono.just(book));

        client.get()
                .uri(getApiPath(book.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Comment.class)
                .hasSize(commentsList.size())
                .value(response -> response.containsAll(commentsList));

        verify(bookRepository, times(1)).findById(book.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 и пустой json при отсутствии комментариев")
    @Test
    public void shouldReturnEmptyCommentsList() throws Exception {
        val book = new Book();
        book.setId(EXISTING_BOOK_ID);

        when(bookRepository.findById(book.getId())).thenReturn(Mono.just(book));

        client.get()
                .uri(getApiPath(book.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Comment.class)
                .hasSize(0)
                .value(response -> response.isEmpty());

        verify(bookRepository, times(1)).findById(book.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 404 при запросе комментариев к несуществующей книге")
    @Test
    public void shouldReturn404() throws Exception {
        val book = new Book();
        book.setId(NO_EXISTING_BOOK_ID);

        when(bookRepository.findById(book.getId())).thenReturn(Mono.empty());

        client.get()
                .uri(getApiPath(book.getId()))
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository, times(1)).findById(book.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 и json с запрашиваемым комментарием")
    @Test
    public void shouldReturn200AndComment() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("2", "reader", LocalDateTime.now(), "text");

        when(bookRepository.findComment(bookId, comment.getId())).thenReturn(Mono.just(comment));

        client.get()
                .uri(getApiPath(bookId) + "/" + comment.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Comment.class)
                .hasSize(1)
                .value(response -> response.get(0).equals(comment));

        verify(bookRepository, times(1)).findComment(bookId, comment.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 404 при запросе несуществующего комментария")
    @Test
    public void shouldReturn404CommentNotFound() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("2", "reader", LocalDateTime.now(), "text");

        when(bookRepository.findComment(bookId, comment.getId())).thenReturn(Mono.empty());

        client.get()
                .uri(getApiPath(bookId) + "/" + comment.getId())
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository, times(1)).findComment(bookId, comment.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 404 при запросе комментария к несуществующей книге")
    @Test
    public void shouldReturn404BookNotFound() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        val commentId = "2";

        when(bookRepository.findComment(bookId, commentId)).thenReturn(Mono.empty());

        client.get()
                .uri(getApiPath(bookId) + "/" + commentId)
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository, times(1)).findComment(bookId, commentId);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 201, путь и json при добавлении комментария к книге")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("Reader", "text comment");
        val commentTime = LocalDateTime.now();
        comment.setTime(commentTime);
        val savedComment = new Comment("21", comment.getAuthor(), commentTime, comment.getText());
        when(bookRepository.addComment(eq(bookId), any(Comment.class))).thenReturn(Mono.just(savedComment));

        client.post()
                .uri(getApiPath(bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(comment), Comment.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .location(getApiPath(bookId) + "/" + savedComment.getId())
                .expectBody(Comment.class)
                .value(response -> response.equals(savedComment));

        verify(bookRepository, times(1)).addComment(eq(bookId), any(Comment.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 400 при добавлении комментария к несуществующей книге")
    @Test
    public void shouldNotAddAndReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        val comment = new Comment("Reader", "textComment");
        when(bookRepository.addComment(eq(bookId), any(Comment.class))).thenReturn(Mono.empty());

        client.post()
                .uri(getApiPath(bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(comment), Comment.class)
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookRepository, times(1)).addComment(eq(bookId), any(Comment.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 и json при обновлении комментария")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("21", "User1", LocalDateTime.now(), "text comment for update");
        val timeUpdate = LocalDateTime.now();
        val savedComment = new Comment(comment.getId(), comment.getAuthor(), timeUpdate, comment.getText());
        when(bookRepository.updateComment(eq(bookId), any(Comment.class))).thenReturn(Mono.just(savedComment));

        client.put()
                .uri(getApiPath(bookId) + "/" + comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(comment), Comment.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Comment.class)
                .value(response -> response.equals(savedComment));

        verify(bookRepository, times(1)).updateComment(eq(bookId), any(Comment.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 400 при обновлении комментария несуществующей книги")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        val comment = new Comment("21", "User1", LocalDateTime.now(), "text comment for update");
        when(bookRepository.updateComment(eq(bookId), any(Comment.class))).thenReturn(Mono.empty());

        client.put()
                .uri(getApiPath(bookId) + "/" + comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(comment), Comment.class)
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookRepository, times(1)).updateComment(eq(bookId), any(Comment.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 400 при обновлении комментария с несовпадающем id в пути")
    @Test
    public void shouldNotUpdateAndReturn400() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val comment = new Comment("21", "User1", LocalDateTime.now(), "text comment for update");

        client.put()
                .uri(getApiPath(bookId) + "/" + comment.getId() + "100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(comment), Comment.class)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 при удалении комментария")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {
        val bookId = EXISTING_BOOK_ID;
        val commentId = "21";
        when(bookRepository.deleteComment(bookId, commentId)).thenReturn(Mono.just(true));

        client.delete()
                .uri(getApiPath(bookId) + "/" + commentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(bookRepository, times(1)).deleteComment(bookId, commentId);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("вернуть 200 при удалении комментария несуществующей книги")
    @Test
    public void shouldNotDeleteAndReturn404() throws Exception {
        val bookId = NO_EXISTING_BOOK_ID;
        val commentId = "21";
        when(bookRepository.deleteComment(bookId, commentId)).thenReturn(Mono.just(false));

        client.delete()
                .uri(getApiPath(bookId) + "/" + commentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(bookRepository, times(1)).deleteComment(bookId, commentId);
        verifyNoMoreInteractions(bookRepository);
    }
}