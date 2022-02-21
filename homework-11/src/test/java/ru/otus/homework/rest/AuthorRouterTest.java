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
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.ErrorMessage;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@Import({AuthorRouter.class})
@WebFluxTest(AuthorRouter.class)
@DisplayName("Author роутер должен ")
class AuthorRouterTest {

    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private WebTestClient client;

    private static final String API = "/api/authors";

    @DisplayName("вернуть 200 и json со всеми авторами")
    @Test
    public void shouldReturnAllAuthors() throws Exception {
        val authorFirst = new Author("1", "Pushkin", "Aleksandr", "Sergeevich");
        val authorSecond = new Author("2", "Goetz", "Brian", "");
        val authorsList = List.of(authorFirst, authorSecond);
        when(authorRepository.findAll()).thenReturn(Flux.fromIterable(authorsList));

        client.get()
                .uri(API)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Author.class)
                .hasSize(authorsList.size())
                .value(response -> response.containsAll(authorsList));

        verify(authorRepository, times(1)).findAll();
        verifyNoMoreInteractions(authorRepository);
    }

    @DisplayName("вернуть 200 и json с запрашиваемым автором")
    @Test
    public void shouldReturn200AndAuthor() throws Exception {
        val authorSecond = new Author("2", "Goetz", "Brian", "");
        when(authorRepository.findById("2")).thenReturn(Mono.just(authorSecond));

        client.get()
                .uri(API + "/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Author.class)
                .value(response -> response.equals(authorSecond));

        verify(authorRepository, times(1)).findById("2");
        verifyNoMoreInteractions(authorRepository);
    }

    @DisplayName("вернуть 404 при запросе несуществующего автора")
    @Test
    public void shouldReturn404() throws Exception {
        when(authorRepository.findById("3")).thenReturn(Mono.empty());

        client.get()
                .uri(API + "/3")
                .exchange()
                .expectStatus().isNotFound();

        verify(authorRepository, times(1)).findById("3");
        verifyNoMoreInteractions(authorRepository);
    }

    @DisplayName("вернуть 201, путь и json при добавлении автора")
    @Test
    public void shouldAddAndReturn201() throws Exception {
        val author = new Author("Goetz", "Brian", "");
        val savedAuthor = new Author("123", "Goetz", "Brian", "");
        when(authorRepository.save(author)).thenReturn(Mono.just(savedAuthor));

        client.post()
                .uri(API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(author), Author.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .location(API + "/" + savedAuthor.getId())
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedAuthor.getId())
                .jsonPath("$.name").isEqualTo(savedAuthor.getName());

        verify(authorRepository, times(1)).save(author);
        verifyNoMoreInteractions(authorRepository);
    }

    @DisplayName("вернуть 200 при обновлении автора")
    @Test
    public void shouldUpdateAndReturn200() throws Exception {
        val author = new Author("123", "Goetz", "Brian", "");
        when(authorRepository.existsById(author.getId())).thenReturn(Mono.just(true));
        when(authorRepository.save(author)).thenReturn(Mono.just(author));

        client.put()
                .uri(API + "/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(author), Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(author.getId())
                .jsonPath("$.name").isEqualTo(author.getName());

        verify(authorRepository, times(1)).existsById(author.getId());
        verify(authorRepository, times(1)).save(author);
        verifyNoMoreInteractions(authorRepository);
    }

    @DisplayName("вернуть 404 при обновлении несуществующего автора")
    @Test
    public void shouldNotUpdateAndReturn404() throws Exception {
        val author = new Author("123", "Goetz", "Brian", "");
        when(authorRepository.existsById(author.getId())).thenReturn(Mono.just(false));

        client.put()
                .uri(API + "/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(author), Author.class)
                .exchange()
                .expectStatus().isNotFound();

        verify(authorRepository, times(1)).existsById(author.getId());
        verifyNoMoreInteractions(authorRepository);
    }

    @DisplayName("вернуть 400 при обновлении автора с несовпадающим id в пути")
    @Test
    public void shouldNotUpdateAndReturn500() throws Exception {
        val author = new Author("123", "Goetz", "Brian", "");

        client.put()
                .uri(API + "/" + author.getId() + "100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(author), Author.class)
                .exchange()
                .expectStatus().isNotFound();

        verifyNoInteractions(authorRepository);
    }

    @DisplayName("вернуть 200 при удалении автора")
    @Test
    public void shouldDeleteAndReturn200() throws Exception {

    }

    @DisplayName("вернуть 400 при попытку удалить автора, связанного с книгой")
    @Test
    public void shouldNotDeleteAndReturn500() throws Exception {
        val author = new Author("123", "Goetz", "Brian", "");
        when(bookRepository.getCountByAuthor(author.getId())).thenReturn(Mono.just(2L));

        client.delete()
                .uri(API + "/" + author.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(result -> result.getMessage().equals("Delete books before delete author"));

        verify(bookRepository, times(1)).getCountByAuthor(author.getId());
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(authorRepository);
    }
}