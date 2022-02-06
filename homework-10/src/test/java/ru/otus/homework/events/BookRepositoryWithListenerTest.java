package ru.otus.homework.events;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.homework.configuration.MongockConfiguration;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.repositories.BookRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("Репозиторий для работы с книгами при наличии листенеров в контексте ")
@Import({MongockConfiguration.class, MongoBookCascadeSaveEventsListener.class})
public class BookRepositoryWithListenerTest {

    @Autowired
    private BookRepository repository;

    @DisplayName("должен сохранять книгу с авторами и жанрами которых еще нет в БД")
    @Test
    void shouldSaveBook() {
        val author = new Author("Bloch", "Joshua", "");
        val genre = new Genre("Java book");

        val actualBook = repository.save(new Book("Effective Java", "", List.of(author), List.of(genre), null));
        assertThat(actualBook.getId()).isNotNull();
        assertThat(actualBook.getAuthorsList()).allMatch(a -> a.getId().length() > 0);
        assertThat(actualBook.getGenresList()).allMatch(g -> g.getId().length() > 0);
    }
}
