package ru.otus.homework.repositories;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Comment repository должен ")
@SpringBootTest
@DirtiesContext
public class CommentRepositoryTest {

    private final long USER_COMMENT_ID = 1;
    private final long USER_COMMENT_FOR_DELETE_ID = 3;
    private final long USER_COMMENT_FOR_DELETE2_ID = 5;
    private final long ADMIN_COMMENT_ID = 2;
    private final long ADMIN_COMMENT_FOR_DELETE_ID = 4;
    private final long BOOK_ID = 2;

    @Autowired
    CommentRepository commentRepository;

    @WithMockUser(username = "user")
    @DisplayName(" получить список комментариев доступных пользователю user")
    @Test
    public void shouldReadComments() {
        val commentList = commentRepository.findAllByBookId(BOOK_ID);
        assertThat(commentList.size()).isGreaterThan(0);
    }

    @WithMockUser(username = "user")
    @DisplayName(" запретить пользователю user обновить комментарий администратора")
    @Test
    public void userShouldNotUpdateAdminComments() {
        val comment = commentRepository.findById(ADMIN_COMMENT_ID).get();
        comment.setText("Updated");
        assertThatThrownBy(() -> commentRepository.save(comment)).isInstanceOf(AccessDeniedException.class);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" разрешить пользователю admin обновить свой комментарий")
    @Test
    public void adminShouldUpdateAdminComments() {
        val comment = commentRepository.findById(ADMIN_COMMENT_ID).get();
        comment.setText("Updated");
        assertThatCode(() -> commentRepository.save(comment)).doesNotThrowAnyException();
    }

    @WithMockUser(username = "user")
    @DisplayName(" разрешить пользователю user обновить свой комментарий")
    @Test
    public void userShouldUpdateUserComments() {
        val comment = commentRepository.findById(USER_COMMENT_ID).get();
        comment.setText("Updated");
        assertThatCode(() -> commentRepository.save(comment)).doesNotThrowAnyException();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" запретить пользователю admin обновить комментарий пользователя")
    @Test
    public void adminShouldNotUpdateUserComments() {
        val comment = commentRepository.findById(USER_COMMENT_ID).get();
        comment.setText("Updated");
        assertThatThrownBy(() -> commentRepository.save(comment)).isInstanceOf(AccessDeniedException.class);
    }

    @WithMockUser(username = "user")
    @DisplayName(" разрешить пользователю user удалить свой комментарий")
    @Test
    public void userShouldDeleteUserComments() {
        val comment = commentRepository.findById(USER_COMMENT_FOR_DELETE_ID).get();
        assertThatCode(() -> commentRepository.delete(comment)).doesNotThrowAnyException();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" разрешить пользователю admin удалить комментарий пользователя")
    @Test
    public void adminShouldDeleteUserComments() {
        val comment = commentRepository.findById(USER_COMMENT_FOR_DELETE2_ID).get();
        assertThatCode(() -> commentRepository.delete(comment)).doesNotThrowAnyException();
    }

    @WithMockUser(username = "user")
    @DisplayName(" запретить пользователю user удалить комментарий администратора")
    @Test
    public void userShouldNotDeleteAdminComments() {
        val comment = commentRepository.findById(ADMIN_COMMENT_ID).get();
        assertThatThrownBy(() -> commentRepository.delete(comment)).isInstanceOf(AccessDeniedException.class);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName(" разрешить пользователю admin удалить свой комментарий")
    @Test
    public void adminShouldDeleteAdminComments() {
        val comment = commentRepository.findById(ADMIN_COMMENT_FOR_DELETE_ID).get();
        assertThatCode(() -> commentRepository.delete(comment)).doesNotThrowAnyException();
    }

}
