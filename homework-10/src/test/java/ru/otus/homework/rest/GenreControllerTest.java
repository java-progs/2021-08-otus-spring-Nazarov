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
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.service.GenreService;
import ru.otus.homework.util.TestUtil;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({GenreController.class})
@WebMvcTest(GenreController.class)
@DisplayName("Genre контроллер должен ")
class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GenreService genreService;

    private static final String API = "/api/genres";

    @DisplayName("вернуть 200 и json со всеми жанрами")
    @Test
    public void shouldReturnAllGenres() throws Exception {
        val genreFirst = new Genre("1", "Test genre");
        val genreSecond = new Genre("2", "Second genre");
        val genresList = List.of(genreFirst, genreSecond);
        given(genreService.getAllGenres()).willReturn(genresList);
        mvc.perform(get(API))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(genreService, times(1)).getAllGenres();
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 200 и json с запрашиваемым жанром")
    @Test
    public void shouldReturn200AndGenre() throws Exception {
        val genreSecond = new Genre("2", "Second genre");

        given(genreService.getGenreById("2")).willReturn(genreSecond);
        mvc.perform(get(API + "/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(genreSecond.getId())));

        verify(genreService, times(1)).getGenreById("2");
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 404 при запросе несуществующего жанра")
    @Test
    public void shouldReturn404() throws Exception {
        when(genreService.getGenreById("3")).thenThrow(new RecordNotFoundException(""));
        mvc.perform(get(API + "/3"))
                .andExpect(status().isNotFound());

        verify(genreService, times(1)).getGenreById("3");
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 201, путь и json при добавлении жанра")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val genre = new Genre("Test");
        val savedGenre = new Genre("123", genre.getName());
        given(genreService.saveGenre(genre)).willReturn(savedGenre);

        mvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(genre))
            ).andExpect(status().is(201))
                .andExpect(redirectedUrl(API + "/123"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo("123")));

        verify(genreService, times(1)).saveGenre(genre);
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 200 и json при обновлении жанра")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val genre = new Genre("21", "Programming");
        given(genreService.updateGenre(genre)).willReturn(true);

        mvc.perform(put(API + "/" + genre.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(genre))
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(genre.getId())));

        verify(genreService, times(1)).getGenreById(genre.getId());
        verify(genreService, times(1)).updateGenre(genre);
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 404 при обновлении несуществующего жанра")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val genre = new Genre("20", "Programming");
        given(genreService.getGenreById(genre.getId()))
                .willThrow(RecordNotFoundException.class);

        mvc.perform(put(API + "/" + genre.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.getJsonBytes(genre))
                ).andExpect(status().isNotFound());

        verify(genreService, times(1)).getGenreById(genre.getId());
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 500 при обновлении жанра с несовпадающим id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() throws Exception {
        val genre = new Genre("20", "Programming");

        mvc.perform(put(API + "/" + genre.getId() + "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.getJsonBytes(genre))
        ).andExpect(status().isBadRequest());

        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 200 при удалении жанра")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {
        doNothing().when(genreService).deleteGenreById("100");

        mvc.perform(delete(API + "/100"))
                .andExpect(status().isOk());

        verify(genreService, times(1)).deleteGenreById("100");
        verifyNoMoreInteractions(genreService);
    }

    @DisplayName("вернуть 500 при попытку удалить жанр, связанный с книгой")
    @Test
    public void shouldNotDeleteAndReturn500() throws Exception {
        doThrow(ViolationOfConstraintException.class).when(genreService)
                .deleteGenreById("101");

        mvc.perform(delete(API + "/101"))
                .andExpect(status().isBadRequest());

        verify(genreService, times(1)).deleteGenreById("101");
        verifyNoMoreInteractions(genreService);
    }
}