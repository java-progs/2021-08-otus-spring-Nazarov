package ru.otus.homework.rest;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.service.AuthorService;
import ru.otus.homework.util.TestUtil;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({AuthorController.class})
@WebMvcTest(AuthorController.class)
@DisplayName("Author контроллер должен ")
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    private static final String API = "/api/authors";

    @DisplayName("вернуть 200 и json со всеми авторами")
    @Test
    public void shouldReturnAllAuthors() throws Exception {
        val authorFirst = new Author("1", "Pushkin", "Aleksandr", "Sergeevich");
        val authorSecond = new Author("2", "Goetz", "Brian", "");
        val authorsList = List.of(authorFirst, authorSecond);
        given(authorService.getAllAuthors()).willReturn(authorsList);
        mvc.perform(get(API))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(authorService, times(1)).getAllAuthors();
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 200 и json с запрашиваемым автором")
    @Test
    public void shouldReturn200AndAuthor() throws Exception {
        val author = new Author("2", "Goetz", "Brian", "");

        given(authorService.getAuthorById("2")).willReturn(author);
        mvc.perform(get(API + "/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(author.getId())))
                .andExpect(jsonPath("$.fullName", equalTo(author.getFullName())));

        verify(authorService, times(1)).getAuthorById("2");
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 404 при запросе несуществующего автора")
    @Test
    public void shouldReturn404() throws Exception {
        when(authorService.getAuthorById("3")).thenThrow(new RecordNotFoundException(""));
        mvc.perform(get(API + "/3"))
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).getAuthorById("3");
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 201, путь и json при добавлении автора")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val author = new Author("Sedgewick", "Robert", "");
        val savedAuthor = new Author("123", author.getSurname(), author.getName(), author.getPatronymic());
        given(authorService.saveAuthor(author)).willReturn(savedAuthor);

        mvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(author))
            ).andExpect(status().is(201))
                .andExpect(redirectedUrl(API + "/123"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo("123")))
                .andExpect(jsonPath("$.fullName", equalTo(savedAuthor.getFullName())));

        verify(authorService, times(1)).saveAuthor(author);
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 200 и json при обновлении автора")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val author = new Author("21", "Goetz", "Brian", "");
        given(authorService.updateAuthor(author)).willReturn(true);

        mvc.perform(put(API + "/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(author))
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(author.getId())));

        verify(authorService, times(1)).getAuthorById(author.getId());
        verify(authorService, times(1)).updateAuthor(author);
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 404 при обновлении несуществующего автора")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val author = new Author("21", "Goetz", "Brian", "");
        given(authorService.getAuthorById(author.getId()))
                .willThrow(RecordNotFoundException.class);

        mvc.perform(put(API + "/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(author))
        ).andExpect(status().isNotFound());

        verify(authorService, times(1)).getAuthorById(author.getId());
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 500 при обновлении автора с несовпадающим id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() throws Exception {
        val author = new Author("201", "Sedgewick", "Robert", "");

        mvc.perform(put(API + "/" + author.getId() + "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(author))
        ).andExpect(status().isBadRequest());

        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 200 при удалении автора")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {
        doNothing().when(authorService).deleteAuthorById("100");

        mvc.perform(delete(API + "/100"))
                .andExpect(status().isOk());

        verify(authorService, times(1)).deleteAuthorById("100");
        verifyNoMoreInteractions(authorService);
    }

    @DisplayName("вернуть 500 при попытку удалить автора, связанного с книгой")
    @Test
    public void shouldNotDeleteAndReturn500() throws Exception {
        doThrow(ViolationOfConstraintException.class).when(authorService)
                .deleteAuthorById("101");

        mvc.perform(delete(API + "/101"))
                .andExpect(status().isBadRequest());

        verify(authorService, times(1)).deleteAuthorById("101");
        verifyNoMoreInteractions(authorService);
    }
}