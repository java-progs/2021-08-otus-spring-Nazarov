package ru.otus.homework.events;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mapping.MappingException;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.repositories.BookRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@DisplayName("Репозиторий для работы с книгами при отсутствии листенеров в контексте ")
public class BookRepositoryWithoutListenerTest {

    @Autowired
    private BookRepository repository;

    @DisplayName("должен бросать MappingException во время сохранения книги с отсутсвующими в БД авторами или жанрами")
    @Test
    void shouldThrowMappingException() {
        val author = new Author("Bloch", "Joshua", "");
        val genre = new Genre("Java book");

        val book = new Book("Effective Java", "", List.of(author), List.of(genre), null);
        assertThatThrownBy(() -> repository.save(book)).isInstanceOf(MappingException.class);
    }
}
