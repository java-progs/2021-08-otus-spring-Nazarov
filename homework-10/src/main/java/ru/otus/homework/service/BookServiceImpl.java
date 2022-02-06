package ru.otus.homework.service;

import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository,
                           GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public long getCountBooks() {
        return bookRepository.count();
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getByAuthor(String authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    @Override
    public List<Book> getByGenre(String genreId) {
        return bookRepository.findByGenre(genreId);
    }

    @Override
    public Book getBookById(String id) throws RecordNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found book with id = %s", id)));
    }

    @Override
    public Book saveBook(Book book) {
        Book savedBook;

        try {
            savedBook = bookRepository.save(book);
        } catch (Exception e) {
            return null;
        }

        return savedBook;
    }

    @Override
    public boolean updateBook(Book book) {
        Book updatedBook;

        try {
            updatedBook = bookRepository.save(book);
        } catch (Exception e) {
            return false;
        }

        return book.equals(updatedBook);
    }

    @Override
    public void deleteBookById(String id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Comment> getAllBookComments(String bookId) {
        return bookRepository.findById(bookId).get().getCommentsList();
    }

    @Override
    public Comment getComment(String bookId, String commentId) throws RecordNotFoundException {
        return bookRepository.findComment(bookId, commentId);
    }

    @Override
    public boolean addComment(String bookId, Comment comment) {
        comment.setId(UUID.randomUUID().toString());
        comment.setTime(getTime());

        return bookRepository.addComment(bookId, comment);
    }

    @Override
    public boolean updateComment(String bookId, Comment comment) {
        Comment currentComment;
        try {
            currentComment = bookRepository.findComment(bookId, comment.getId());
        } catch (Exception e) {
            return false;
        }

        currentComment.setTime(getTime());
        currentComment.setText(comment.getText());

        comment = currentComment;
        return bookRepository.updateComment(bookId, comment);
    }

    @Override
    public boolean deleteComment(String bookId, String commentId) {
        return bookRepository.deleteComment(bookId, commentId);
    }

    private LocalDateTime getTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
