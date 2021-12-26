package ru.otus.homework.service;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.AuthorRepository;
import ru.otus.homework.dao.BookRepository;
import ru.otus.homework.dao.GenreRepository;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Transactional(readOnly = true)
    public long getCountBooks() {
        return bookRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(long id) throws RecordNotFoundException {
        val optionalBook = bookRepository.getById(id);

        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new RecordNotFoundException(String.format("Not found book with id = %d", id));
        }
    }

    @Override
    @Transactional
    public boolean saveBook(Book book) {

        try {
            bookRepository.save(book);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean saveBook(String name, String isbn, Long[] authorsIds, Long[] genresIds) {
        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        for (long id : authorsIds) {
            Optional<Author> optionalAuthor = authorRepository.getById(id);
            if (optionalAuthor.isPresent()) {
                authorsList.add(optionalAuthor.get());
            } else {
                return false;
            }
        }

        for (long id : genresIds) {
            Optional<Genre> optionalGenre = genreRepository.getById(id);
            if (optionalGenre.isPresent()) {
                genresList.add(optionalGenre.get());
            } else {
                return false;
            }
        }

        return saveBook(new Book(0, name, isbn, authorsList, genresList));
    }

    @Override
    @Transactional
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
    @Transactional
    public boolean updateBook(long bookId, String name, String isbn, Long[] authorsIds, Long[] genresIds) {
        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        for (long id : authorsIds) {
            Optional<Author> optionalAuthor = authorRepository.getById(id);
            if (optionalAuthor.isPresent()) {
                authorsList.add(optionalAuthor.get());
            } else {
                return false;
            }
        }

        for (long id : genresIds) {
            Optional<Genre> optionalGenre = genreRepository.getById(id);
            if (optionalGenre.isPresent()) {
                genresList.add(optionalGenre.get());
            } else {
                return false;
            }
        }

        return updateBook(new Book(bookId, name, isbn, authorsList, genresList));
    }

    @Override
    @Transactional
    public boolean deleteBookById(long id) {
        var deletedRows = 0;

        try {
            deletedRows = bookRepository.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return deletedRows > 0;
    }

}
