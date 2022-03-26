package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.AuthorRepository;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public long getCountBooks() {
        return bookRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(long id) throws RecordNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found book with id = %d", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllByAuthor(long authorId) {
        return bookRepository.findAllBookByAuthor(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllByGenre(long genreId) {
        return bookRepository.findAllBookByGenre(genreId);
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
            val optionalAuthor = authorRepository.findById(id);
            if (optionalAuthor.isPresent()) {
                authorsList.add(optionalAuthor.get());
            } else {
                return false;
            }
        }

        for (long id : genresIds) {
            val optionalGenre = genreRepository.findById(id);
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
            val optionalAuthor = authorRepository.findById(id);
            if (optionalAuthor.isPresent()) {
                authorsList.add(optionalAuthor.get());
            } else {
                return false;
            }
        }

        for (long id : genresIds) {
            Optional<Genre> optionalGenre = genreRepository.findById(id);
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
    public void deleteBookById(long id) {
        bookRepository.deleteById(id);
    }

}
