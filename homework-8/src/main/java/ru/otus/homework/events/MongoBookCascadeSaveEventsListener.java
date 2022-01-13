package ru.otus.homework.events;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.GenreRepository;
import ru.otus.homework.domain.Book;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MongoBookCascadeSaveEventsListener extends AbstractMongoEventListener<Book> {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Book> event) {
        super.onBeforeConvert(event);
        val book = event.getSource();

        if (book.getAuthorsList() != null) {
            book.getAuthorsList().stream().filter(a -> Objects.isNull(a.getId())).forEach(authorRepository::save);
        }

        if (book.getGenresList() != null) {
            book.getGenresList().stream().filter(g -> Objects.isNull(g.getId())).forEach(genreRepository::save);
        }
    }
}
