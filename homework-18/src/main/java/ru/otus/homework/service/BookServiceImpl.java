package ru.otus.homework.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.BookRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService,
                           GenreService genreService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public long getCountBooks() {
        return bookRepository.count();
    }

    @HystrixCommand(commandKey = "readFromDbKey", fallbackMethod = "getEmptyList")
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @HystrixCommand(commandKey = "readFromDbKey", fallbackMethod = "getEmptyList")
    @Override
    public List<Book> getByAuthor(String authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    @HystrixCommand(commandKey = "readFromDbKey", fallbackMethod = "getEmptyList")
    @Override
    public List<Book> getByGenre(String genreId) {
        return bookRepository.findByGenre(genreId);
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public Book getBookById(String id) throws RecordNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found book with id = %s", id)));
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public Book saveBook(Book book) {
        Book savedBook = bookRepository.save(book);

        return savedBook;
    }

    @Override
    public Book saveBook(String name, String isbn, List<String> authorsId, List<String> genresId) {
        List<Author> authorsList = authorService.getAllById(authorsId);
        List<Genre> genresList = genreService.getAllById(genresId);

        return saveBook(new Book(name, isbn, authorsList, genresList, null));
    }


    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public boolean updateBook(Book book) {
        Book updatedBook = bookRepository.save(book);

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

        List<Author> authorsList = authorService.getAllById(authorsId);
        List<Genre> genresList = genreService.getAllById(genresId);

        currentBook.setName(name);
        currentBook.setIsbn(isbn);
        currentBook.setAuthorsList(authorsList);
        currentBook.setGenresList(genresList);

        return updateBook(currentBook);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public void deleteBookById(String id) {
        bookRepository.deleteById(id);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public List<Comment> getAllBookComments(String bookId) {
        return bookRepository.findById(bookId).get().getCommentsList();
    }

    @HystrixCommand(commandKey = "readFromDbKey")
    @Override
    public Comment getComment(String bookId, String commentId) {
        return bookRepository.findComment(bookId, commentId);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public boolean addComment(String bookId, Comment comment) {
        comment.setId(UUID.randomUUID().toString());
        comment.setTime(getTime());

        return bookRepository.addComment(bookId, comment);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public boolean updateComment(String bookId, String commentId, String text) {
        Comment comment;

        comment = bookRepository.findComment(bookId, commentId);

        if (comment == null) {
            return false;
        }

        comment.setTime(getTime());
        comment.setText(text);

        return bookRepository.updateComment(bookId, comment);
    }

    @HystrixCommand(commandKey = "otherDbOperationKey")
    @Override
    public boolean deleteComment(String bookId, String commentId) {
        return bookRepository.deleteComment(bookId, commentId);
    }

    private List<Book> getEmptyList() {
        return new ArrayList<>();
    }

    private List<Book> getEmptyList(String id) {
        return new ArrayList<>();
    }

    private LocalDateTime getTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
