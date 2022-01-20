package ru.otus.homework.events;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.repositories.GenreRepository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataMongoTest
@DisplayName("Репозиторий для работы с жанрами при наличии листенера в контексте ")
@Import(MongoGenreDeleteEventsListener.class)
public class GenreRepositoryWithListenerTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private MongoOperations operations;

    @DisplayName("должен бросать исключение при попытке удаления жанра имеющего книги в базе ")
    @Test
    void shouldThrowException() {
        val query = Query.query(Criteria.where("name").is("Programming"));
        val genre = operations.findOne(query, Genre.class);
        assertThat(genre).isNotNull();
        assertThatThrownBy(() -> repository.deleteById(genre.getId()))
                .isInstanceOf(ViolationOfConstraintException.class);
    }

    @DisplayName("не должен бросать исключение при попытке удаления жанра не имеющего книги в базе ")
    @Test
    void shouldNotThrowException() {
        val query = Query.query(Criteria.where("name").is("Kotlin books"));
        val genre = operations.findOne(query, Genre.class);
        assertThat(genre).isNotNull();
        assertThatCode(() -> repository.deleteById(genre.getId()))
                .doesNotThrowAnyException();
    }
}
