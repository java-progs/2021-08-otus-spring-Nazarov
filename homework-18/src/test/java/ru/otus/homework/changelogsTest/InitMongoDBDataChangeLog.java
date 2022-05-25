package ru.otus.homework.changelogsTest;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import lombok.val;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    private Author authorPushkin;
    private Author authorGutierrez;

    private Genre genreBelletristic;
    private Genre genreProgramming;
    private Genre genreSpringBook;

    private Book bookFisherman;
    private Book bookSpring;

    private Comment commentForSpring;
    private Comment otherCommentForSpring;

    @ChangeSet(order = "000", id = "dropDB", author = "user", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "user", runAlways = true)
    public void initAuthors(MongockTemplate template) {
        authorPushkin = template.save(new Author("Pushkin", "Aleksandr", "Sergeevich"));
        authorGutierrez = template.save(new Author("Gutierrez", "Felipe", ""));
        template.save(new Author("Isakova", "Svetlana", ""));
        template.save(new Author("Jemerov", "Dmitriy", ""));
        template.save(new Author("Martin", "Robert", ""));
    }

    @ChangeSet(order = "002", id = "initGenres", author = "user", runAlways = true)
    public void initGenres(MongockTemplate template) {
        genreBelletristic = template.save(new Genre("Belletristic"));
        genreProgramming = template.save(new Genre("Programming"));
        genreSpringBook = template.save(new Genre("Spring books"));
        template.save(new Genre("Kotlin books"));
    }

    @ChangeSet(order = "003", id = "initBooks", author = "user", runAlways = true)
    public void initBooks(MongockTemplate template) {
        bookFisherman = template.save(new Book("The Fisherman and the GoldFish", "", List.of(authorPushkin), List.of(genreBelletristic), null));
        bookSpring = template.save(new Book("Spring boot 2", "", List.of(authorGutierrez), List.of(genreProgramming, genreSpringBook), null));
        template.save(new Book("Book for delete", "", List.of(authorGutierrez), List.of(genreProgramming, genreSpringBook), null));
        template.save(new Book("Book for update", "", List.of(authorGutierrez), List.of(genreProgramming, genreSpringBook), null));
        template.save(new Book("Other book for update", "", List.of(authorGutierrez), List.of(genreProgramming, genreSpringBook), null));
    }

    @ChangeSet(order = "004", id = "addComments", author = "user", runAlways = true)
    public void addComments(MongockTemplate template) {
        val query = new Query(Criteria.where("id").is(bookSpring.getId()));
        val update = new Update();

        commentForSpring = new Comment(UUID.randomUUID().toString(),"user 1", LocalDateTime.now(), "Comment for spring book");
        otherCommentForSpring = new Comment(UUID.randomUUID().toString(), "Uses", LocalDateTime.now(), "Text comment");
        update.set("commentsList", List.of(commentForSpring, otherCommentForSpring));

        template.updateFirst(query, update, Book.class);
    }

}
