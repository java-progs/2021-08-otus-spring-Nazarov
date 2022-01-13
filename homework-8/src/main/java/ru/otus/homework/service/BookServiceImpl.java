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
import java.util.stream.Collectors;

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
    public Book getBookById(String id) throws RecordNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found book with id = %d", id)));
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
    public Book saveBook(String name, String isbn, List<String> authorsId, List<String> genresId) {
        authorsId.stream().collect(Collectors.toList());
        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        authorRepository.findAllById(authorsId).forEach(a -> authorsList.add(a));
        genreRepository.findAllById(genresId).forEach(g -> genresList.add(g));
        return saveBook(new Book(name, isbn, authorsList, genresList, null));
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
    public boolean updateBook(String bookId, String name, String isbn, List<String> authorsId, List<String> genresId) {
        Book currentBook;

        try {
            currentBook = getBookById(bookId);
        } catch (RecordNotFoundException e) {
            return false;
        }

        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        authorRepository.findAllById(authorsId).forEach(a -> authorsList.add(a));
        genreRepository.findAllById(genresId).forEach(g -> genresList.add(g));

        currentBook.setName(name);
        currentBook.setIsbn(isbn);
        currentBook.setAuthorsList(authorsList);
        currentBook.setGenresList(genresList);

        return updateBook(currentBook);
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
    public Comment getComment(String bookId, String commentId) {
        return bookRepository.findComment(bookId, commentId);
    }

    @Override
    public boolean addComment(String bookId, Comment comment) {
        comment.setId(UUID.randomUUID().toString());
        comment.setTime(getTime());

        return bookRepository.addComment(bookId, comment);
    }

    @Override
    public boolean updateComment(String bookId, String commentId, String text) {
        Comment comment;
        try {
            comment = bookRepository.findComment(bookId, commentId);
        } catch (Exception e) {
            return false;
        }

        comment.setTime(getTime());
        comment.setText(text);

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
