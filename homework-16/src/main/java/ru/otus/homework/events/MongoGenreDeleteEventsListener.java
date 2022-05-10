package ru.otus.homework.events;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class MongoGenreDeleteEventsListener extends AbstractMongoEventListener<Genre> {

    private final BookRepository bookRepository;
    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Genre> event) {
        super.onBeforeDelete(event);
        val source = event.getSource();
        val id = source.get("_id").toString();
        val countBook = bookRepository.getCountByGenre(id);

        if (countBook > 0) {
            throw new ViolationOfConstraintException(
                    String.format("Error delete genre. Delete %s related book before delete genre",
                            countBook));
        }
    }
}
