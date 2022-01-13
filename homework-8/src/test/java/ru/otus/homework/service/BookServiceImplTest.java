package ru.otus.homework.service;

import lombok.val;
import org.bson.types.ObjectId;
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
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.events.MongoBookCascadeSaveEventsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@ComponentScan({"ru.otus.homework.service"})
@DisplayName("Сервис для работы с книгами должен ")
@EnableConfigurationProperties
@Import(MongoBookCascadeSaveEventsListener.class)
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoOperations operations;

    @DisplayName("возвращать корректное количество книг")
    @Test
    void shouldReturnCountBook() {
        val expectedCount = bookService.getCountBooks();
        val actualCount = operations.findAll(Book.class).size();
        assertThat(expectedCount).isEqualTo(actualCount);
    }

    @DisplayName("возвращать список книг")
    @Test
    void shouldReturnAllBook() {
        val expectedBookList = bookService.getAllBooks();
        val actualBooksList = operations.findAll(Book.class);

        assertThat(expectedBookList).size().isEqualTo(actualBooksList.size());
        assertThat(expectedBookList).containsExactlyInAnyOrderElementsOf(actualBooksList);
    }

    @DisplayName("возвращать книгу по id")
    @Test
    void shouldGetBookById() {
        val expectedBook = bookService.getAllBooks().get(0);
        assertThatCode(() -> bookService.getBookById(expectedBook.getId()))
                .doesNotThrowAnyException();

        val actualBook = getBookFromDbById(expectedBook.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("добавлять книгу")
    @Test
    void shouldSaveBook() {
        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        authorsList.add(new Author("Lermontov", "Mikhail", ""));
        genresList.add(new Genre("Test genre"));
        val book = new Book("Test", null, authorsList, genresList, null);

        val expectedBook = bookService.saveBook(book);
        val actualBook = getBookFromDbById(expectedBook.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("добавлять книгу через список полей")
    @Test
    void shouldSaveBookThroughFields() {
        var query = Query.query(Criteria.where("surname").is("Isakova").and("name").is("Svetlana"));
        val authorIsakova = operations.find(query, Author.class).get(0);
        query = Query.query(Criteria.where("surname").is("Jemerov").and("name").is("Dmitriy"));
        val authorJemerov = operations.find(query, Author.class).get(0);
        query = Query.query(Criteria.where("name").is("Programming"));
        val genreProgramming = operations.find(query, Genre.class).get(0);

        val expectedBook = bookService.saveBook("Kotlin in action", "",
                List.of(authorIsakova.getId(), authorJemerov.getId()), List.of(genreProgramming.getId()));
        val actualBook = getBookFromDbById(expectedBook.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("обновлять книгу")
    @Test
    void shouldUpdateBook() {
        val book = getBookFromDbByName("Book for update");

        val expectedBook = new Book(book.getId(), book.getName() + " (Updated)", book.getIsbn(),
                book.getAuthorsList(), book.getGenresList(), book.getCommentsList());
        val result = bookService.updateBook(expectedBook);
        assertThat(result).isTrue();

        val actualBook = getBookFromDbById(expectedBook.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("обновлять книгу через список полей")
    @Test
    void shouldUpdateBookThroughFields() {
        val book = getBookFromDbByName("Other book for update");

        val expectedBook = new Book(book.getId(), book.getName() + " (Updated through fields)", book.getIsbn(),
                book.getAuthorsList(), book.getGenresList(), book.getCommentsList());
        val result = bookService.updateBook(expectedBook.getId(), expectedBook.getName(),
                expectedBook.getIsbn(),
                expectedBook.getAuthorsList().stream().map(a -> a.getId()).collect(Collectors.toList()),
                expectedBook.getGenresList().stream().map(g -> g.getId()).collect(Collectors.toList()));
        assertThat(result).isTrue();

        val actualBook = getBookFromDbById(expectedBook.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(actualBook);
    }

    @DisplayName("удалять книгу")
    @Test
    void shouldDeleteBook() {
        val book = getBookFromDbByName("Book for delete");
        bookService.deleteBookById(book.getId());
        assertThatThrownBy(() -> getBookFromDbById(book.getId())).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @DisplayName("возвращать все комментарии книги")
    @Test
    void shouldGetBookComments() {
        val actualBook = getBookFromDbByName("Spring boot 2");

        val expectedComments = bookService.getAllBookComments(actualBook.getId());
        assertThat(expectedComments).containsExactlyInAnyOrderElementsOf(actualBook.getCommentsList());
    }

    @DisplayName("возвращать комментарий к книге")
    @Test
    void shouldReturnBookCommentById() {
        val actualBook = getBookFromDbByName("Spring boot 2");
        val actualComment = actualBook.getCommentsList().get(0);

        val expectedComment = bookService.getComment(actualBook.getId(), actualComment.getId());
        assertThat(expectedComment).usingRecursiveComparison().isEqualTo(actualComment);
    }

    @DisplayName("добавлять комментарий к книге")
    @Test
    void shouldAddComment() {
        val book = bookService.getAllBooks().get(0);
        val comment = new Comment("User 1", "Comment text");
        val result = bookService.addComment(book.getId(), comment);
        assertThat(result).isTrue();

        val actualBook = getBookFromDbById(book.getId());
        assertThat(actualBook.getCommentsList()).contains(comment);
    }

    @DisplayName("обновлять комментарий")
    @Test
    void shouldUpdateComment() {
        val book = getBookFromDbByName("Spring boot 2");
        var comment = book.getCommentsList().get(0);
        comment.setText(comment.getText() + " (Updated)");

        val result = bookService.updateComment(book.getId(), comment.getId(), comment.getText());
        assertThat(result).isTrue();

        val expectedComment = bookService.getComment(book.getId(), comment.getId());
        val actualBook = getBookFromDbById(book.getId());
        assertThat(actualBook.getCommentsList()).contains(expectedComment);
    }

    @DisplayName("удалять комментарий")
    @Test
    void shouldDeleteComment() {
        val book = getBookFromDbByName("Spring boot 2");
        val comment = book.getCommentsList().get(0);

        var actualBook = getBookFromDbById(book.getId());
        assertThat(actualBook.getCommentsList()).containsAnyElementsOf(List.of(comment));

        val result = bookService.deleteComment(book.getId(), comment.getId());
        assertThat(result).isTrue();

        actualBook = getBookFromDbById(book.getId());
        assertThat(actualBook.getCommentsList()).doesNotContain(comment);
    }

    private Book getBookFromDbById(String id) {
        val query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        return operations.find(query, Book.class).get(0);
    }

    private Book getBookFromDbByName(String name) {
        val query = Query.query(Criteria.where("name").is(name));
        return operations.find(query, Book.class).get(0);
    }

}