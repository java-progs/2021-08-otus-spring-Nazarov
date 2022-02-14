package ru.otus.homework.events;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.homework.configuration.MongockConfiguration;
import ru.otus.homework.domain.Author;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.repositories.AuthorRepository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataMongoTest
@DisplayName("Репозиторий для работы с авторами при наличии листенера в контексте ")
@Import({MongockConfiguration.class, MongoAuthorDeleteEventsListener.class})
public class AuthorRepositoryWithListenerTest {

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private MongoOperations operations;

    @DisplayName("должен бросать исключение при попытке удаления автора имеющего книги в базе ")
    @Test
    void shouldThrowException() {
        val query = Query.query(Criteria.where("surname").is("Gutierrez"));
        val author = operations.findOne(query, Author.class);
        assertThat(author).isNotNull();
        assertThatThrownBy(() -> repository.deleteById(author.getId()))
                .isInstanceOf(ViolationOfConstraintException.class);
    }

    @DisplayName("не должен бросать исключение при попытке удаления автора не имеющего книги в базе ")
    @Test
    void shouldNotThrowException() {
        val query = Query.query(Criteria.where("surname").is("Martin"));
        val author = operations.findOne(query, Author.class);
        assertThat(author).isNotNull();
        assertThatCode(() -> repository.deleteById(author.getId()))
                .doesNotThrowAnyException();
    }
}
