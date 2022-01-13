package ru.otus.homework.events;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.exception.ViolationOfConstraintException;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class MongoAuthorDeleteEventsListener extends AbstractMongoEventListener<Author> {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final MongoTemplate template;

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Author> event) {
        super.onBeforeDelete(event);
        val source = event.getSource();
        val id = source.get("_id").toString();

        val query = Query.query(Criteria.where("authorsList.$id").is(new ObjectId(id)));
        val countBook = template.count(query, Book.class);
        if (countBook > 0) {
            throw new ViolationOfConstraintException(
                    String.format("Error delete author. Delete %s related book before delete author",
                            countBook));
        }
    }
}
