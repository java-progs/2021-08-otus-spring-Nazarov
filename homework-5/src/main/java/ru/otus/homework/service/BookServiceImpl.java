package ru.otus.homework.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.AuthorDao;
import ru.otus.homework.dao.BookDao;
import ru.otus.homework.dao.GenreDao;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;
import ru.otus.homework.exception.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookServiceImpl(BookDao bookDao, AuthorDao authorDao, GenreDao genreDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @Override
    public int getCountBooks() {
        return bookDao.count();
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDao.getAll();
    }

    @Override
    public Book getBookById(long id) throws RecordNotFoundException {
        try {
            return bookDao.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Not found book with id = %d", id));
        }
    }

    @Override
    public List<Book> getBooksByAuthor(long authorId) {
        return bookDao.getBooksByAuthor(authorId);
    }

    @Override
    public List<Book> getBooksByGenre(long genreId) {
        return bookDao.getBooksByGenre(genreId);
    }

    @Override
    public boolean addBook(Book book) {
        try {
            bookDao.insert(book);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean addBook(String name, String isbn, Long[] authorsIds, Long[] genresIds) {

        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        try {
            for (long id : authorsIds) {
                authorsList.add(authorDao.getById(id));
            }

            for (long id : genresIds) {
                genresList.add(genreDao.getById(id));
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        return addBook(new Book(name, isbn, authorsList, genresList));
    }

    @Override
    @Transactional
    public boolean updateBook(Book book) {
        Book updatedBook;

        try {
            updatedBook = bookDao.update(book);
        } catch (Exception e) {
            return false;
        }

        return book.equals(updatedBook);
    }

    @Override
    @Transactional
    public boolean updateBook(long id, String name, String isbn, Long[] authorsIds, Long[] genresIds) {
        List<Author> authorsList = new ArrayList<>();
        List<Genre> genresList = new ArrayList<>();

        try {
            for (long i : authorsIds) {
                authorsList.add(authorDao.getById(i));
            }

            for (long i : genresIds) {
                genresList.add(genreDao.getById(i));
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        return updateBook(new Book(id, name, isbn, authorsList, genresList));
    }

    @Override
    @Transactional
    public boolean deleteBookById(long id) {
        try {
            bookDao.deleteById(id);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
