package ru.otus.homework.rest;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.homework.domain.ErrorMessage;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@Import({GenreRouter.class, GenreHandler.class})
@WebFluxTest({GenreRouter.class})
@DisplayName("Genre роутер должен ")
class GenreRouterTest {

    @MockBean
    private GenreRepository genreRepository;
    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private WebTestClient client;

    private static final String API = "/api/genres";

    @DisplayName("вернуть 200 и json со всеми жанрами")
    @Test
    public void shouldReturnAllGenres() throws Exception {
        val genreFirst = new Genre("1", "Test genre");
        val genreSecond = new Genre("2", "Second genre");
        val genresList = List.of(genreFirst, genreSecond);
        when(genreRepository.findAll()).thenReturn(Flux.fromIterable(genresList));

        client.get()
                .uri(API)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Genre.class)
                .hasSize(genresList.size())
                .value(response -> response.containsAll(genresList));

        verify(genreRepository, times(1)).findAll();
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 200 и json с запрашиваемым жанром")
    @Test
    public void shouldReturn200AndGenre() throws Exception {
        val genreSecond = new Genre("2", "Second genre");
        when(genreRepository.findById("2")).thenReturn(Mono.just(genreSecond));

        client.get()
                .uri(API + "/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Genre.class)
                .value(response -> response.equals(genreSecond));

        verify(genreRepository, times(1)).findById("2");
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 404 при запросе несуществующего жанра")
    @Test
    public void shouldReturn404() throws Exception {
        when(genreRepository.findById("3")).thenReturn(Mono.empty());

        client.get()
                .uri(API + "/3")
                .exchange()
                .expectStatus().isNotFound();

        verify(genreRepository, times(1)).findById("3");
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 201, путь и json при добавлении жанра")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val genre = new Genre("Test");
        val savedGenre = new Genre("123", "Test");
        when(genreRepository.save(genre)).thenReturn(Mono.just(savedGenre));

        client.post()
                .uri(API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(genre), Genre.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .location(API + "/" + savedGenre.getId())
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedGenre.getId())
                .jsonPath("$.name").isEqualTo(savedGenre.getName());

        verify(genreRepository, times(1)).save(genre);
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 200 при обновлении жанра")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val genre = new Genre("123", "Test");
        when(genreRepository.existsById(genre.getId())).thenReturn(Mono.just(true));
        when(genreRepository.save(genre)).thenReturn(Mono.just(genre));

        client.put()
                .uri(API + "/" + genre.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(genre), Genre.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(genre.getId())
                .jsonPath("$.name").isEqualTo(genre.getName());

        verify(genreRepository, times(1)).existsById(genre.getId());
        verify(genreRepository, times(1)).save(genre);
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 404 при обновлении несуществующего жанра")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val genre = new Genre("123", "Test");
        when(genreRepository.existsById(genre.getId())).thenReturn(Mono.just(false));

        client.put()
                .uri(API + "/" + genre.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(genre), Genre.class)
                .exchange()
                .expectStatus().isNotFound();

        verify(genreRepository, times(1)).existsById(genre.getId());
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 400 при обновлении жанра с несовпадающим id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() throws Exception {
        val genre = new Genre("123", "Test");

        client.put()
                .uri(API + "/" + genre.getId() + "100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(genre), Genre.class)
                .exchange()
                .expectStatus().isNotFound();

        verifyNoInteractions(genreRepository);
    }

    @DisplayName("вернуть 200 при удалении жанра")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {
        val genre = new Genre("123", "Test");
        when(genreRepository.deleteById(genre.getId())).thenReturn(Mono.empty());
        when(bookRepository.getCountByGenre(genre.getId())).thenReturn(Mono.just(0L));

        client.delete()
                .uri(API + "/" + genre.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(bookRepository, times(1)).getCountByGenre(genre.getId());
        verifyNoMoreInteractions(bookRepository);
        verify(genreRepository, times(1)).deleteById(genre.getId());
        verifyNoMoreInteractions(genreRepository);
    }

    @DisplayName("вернуть 400 при попытку удалить жанр, связанный с книгой")
    @Test
    public void shouldNotDeleteAndReturn500() throws Exception {
        val genre = new Genre("123", "Test");
        when(bookRepository.getCountByGenre(genre.getId())).thenReturn(Mono.just(2L));

        client.delete()
                .uri(API + "/" + genre.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(result -> result.getMessage().equals("Delete books before delete genre"));

        verify(bookRepository, times(1)).getCountByGenre(genre.getId());
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(genreRepository);
    }
}