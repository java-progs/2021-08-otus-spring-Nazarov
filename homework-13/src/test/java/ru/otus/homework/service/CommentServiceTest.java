package ru.otus.homework.service;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Comment service должен ")
@SpringBootTest
@DirtiesContext
public class CommentServiceTest {

    private final long USER_COMMENT_ID = 1;
    private final long USER_COMMENT_FOR_DELETE_ID = 3;
    private final long USER_COMMENT_FOR_DELETE2_ID = 5;
    private final long ADMIN_COMMENT_ID = 2;
    private final long ADMIN_COMMENT_FOR_DELETE_ID = 4;
    private final long BOOK_ID = 2;

    @Autowired
    CommentService commentService;

    @WithMockUser(username = "user")
    @DisplayName(" получить список комментариев доступных пользователю user")
    @Test
    public void shouldReadComments() {
        val commentList = commentService.getBookComments(BOOK_ID);
        assertThat(commentList.size()).isGreaterThan(0);
    }

    @WithMockUser(username = "user")
    @DisplayName(" запретить пользователю user обновить комментарий администратора")
    @Test
    public void userShouldNotUpdateAdminComments() throws Exception {
        val comment = commentService.getCommentById(ADMIN_COMMENT_ID);
        comment.setText("comment text");
        assertThatThrownBy(() -> commentService.updateComment(comment))
                .isInstanceOf(AccessDeniedException.class);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" разрешить пользователю admin обновить свой комментарий")
    @Test
    public void adminShouldUpdateAdminComments() throws Exception {
        val comment = commentService.getCommentById(ADMIN_COMMENT_ID);
        comment.setText("comment text");
        assertThatCode(() -> commentService.updateComment(comment))
                .doesNotThrowAnyException();
    }

    @WithMockUser(username = "user")
    @DisplayName(" разрешить пользователю user обновить свой комментарий")
    @Test
    public void userShouldUpdateUserComments() throws Exception {
        val comment = commentService.getCommentById(USER_COMMENT_ID);
        comment.setText("comment text");
        assertThatCode(() -> commentService.updateComment(comment))
                .doesNotThrowAnyException();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" запретить пользователю admin обновить комментарий пользователя")
    @Test
    public void adminShouldNotUpdateUserComments() throws Exception {
        val comment = commentService.getCommentById(USER_COMMENT_ID);
        comment.setText("comment text");
        assertThatThrownBy(() -> commentService.updateComment(comment))
                .isInstanceOf(AccessDeniedException.class);
    }

    @WithMockUser(username = "user")
    @DisplayName(" разрешить пользователю user удалить свой комментарий")
    @Test
    public void userShouldDeleteUserComments() throws Exception {
        val comment = commentService.getCommentById(USER_COMMENT_FOR_DELETE_ID);
        comment.setText("comment text");
        assertThatCode(() -> commentService.deleteComment(comment))
                .doesNotThrowAnyException();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" разрешить пользователю admin удалить комментарий пользователя")
    @Test
    public void adminShouldDeleteUserComments() throws Exception {
        val comment = commentService.getCommentById(USER_COMMENT_FOR_DELETE2_ID);
        comment.setText("comment text");
        assertThatCode(() -> commentService.deleteComment(comment))
                .doesNotThrowAnyException();
    }

    @WithMockUser(username = "user")
    @DisplayName(" запретить пользователю user удалить комментарий администратора")
    @Test
    public void userShouldNotDeleteAdminComments() throws Exception {
        val comment = commentService.getCommentById(ADMIN_COMMENT_ID);
        comment.setText("comment text");
        assertThatThrownBy(() -> commentService.deleteComment(comment))
                .isInstanceOf(AccessDeniedException.class);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" разрешить пользователю admin удалить свой комментарий")
    @Test
    public void adminShouldDeleteAdminComments() throws Exception {
        val comment = commentService.getCommentById(ADMIN_COMMENT_FOR_DELETE_ID);
        comment.setText("comment text");
        assertThatCode(() -> commentService.deleteComment(comment))
                .doesNotThrowAnyException();
    }

}
