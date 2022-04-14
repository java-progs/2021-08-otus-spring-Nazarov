package ru.otus.homework.domain;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.homework.service.BookService;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final BookService bookService;

    public UserMongo userToMongo(User user) {
        val userMongo = new UserMongo();
        userMongo.setName(user.getName());
        userMongo.setPassword(user.getPassword());
        return userMongo;
    }

    public AuthorMongo authorToMongo(Author author) {
        val authorMongo = new AuthorMongo();
        authorMongo.setSurname(author.getName());
        authorMongo.setName(author.getName());
        authorMongo.setPatronymic(author.getPatronymic());
        authorMongo.setOldId(author.getId());
        return authorMongo;
    }

    public GenreMongo genreToMongo(Genre genre) {
        val genreMongo = new GenreMongo();
        genreMongo.setName(genre.getName());
        genreMongo.setOldId(genre.getId());
        return genreMongo;
    }

    public BookMongo bookToMongo(Book book) {
        return bookService.convertBookToBookMongo(book);
    }

}
