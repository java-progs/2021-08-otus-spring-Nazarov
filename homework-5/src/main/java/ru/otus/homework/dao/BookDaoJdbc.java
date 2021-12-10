package ru.otus.homework.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BookDaoJdbc implements BookDao {

    private final NamedParameterJdbcOperations jdbc;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookDaoJdbc(NamedParameterJdbcOperations jdbc, AuthorDao authorDao, GenreDao genreDao) {
        this.jdbc = jdbc;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @Override
    public int count() {
        return jdbc.getJdbcOperations().queryForObject("select count(*) from book", Integer.class);
    }

    @Override
    public Book getById(long id) {
        Book book;

        Book _book = jdbc.queryForObject("select * from book where id = :id",
                Map.of("id", id), new BookMapper()
        );

        List<Author> authorsList = authorDao.getBookAuthors(_book);
        List<Genre> genresList = genreDao.getBookGenres(_book);
        book = new Book(_book.getId(), _book.getName(), _book.getIsbn(), authorsList, genresList);

        return book;
    }

    @Override
    public List<Book> getAll() {
        List<Book> booksList = new ArrayList<>();
        List<Book> _booksList = jdbc.query("select * from book", new BookMapper());
        for (Book book : _booksList) {
            List<Author> authorsList = authorDao.getBookAuthors(book);
            List<Genre> genresList = genreDao.getBookGenres(book);
            booksList.add(new Book(book.getId(), book.getName(), book.getIsbn(), authorsList, genresList));
        }

        return booksList;
    }

    @Override
    public List<Book> getBooksByAuthor(long id) {
        List<Book> booksList = new ArrayList<>();
        List<Book> _booksList = jdbc.query("select b.* from book b, book_author a where b.id = a.book_id and a.author_id = :authorId",
                Map.of("authorId", id), new BookMapper());
        for (Book book : _booksList) {
            List<Author> authorsList = authorDao.getBookAuthors(book);
            List<Genre> genresList = genreDao.getBookGenres(book);
            booksList.add(new Book(book.getId(), book.getName(), book.getIsbn(), authorsList, genresList));
        }

        return booksList;
    }

    @Override
    public List<Book> getBooksByGenre(long id) {
        List<Book> booksList = new ArrayList<>();
        List<Book> _booksList = jdbc.query("select b.* from book b, book_genre g where b.id = g.book_id and g.genre_id = :genreId",
                Map.of("genreId", id), new BookMapper());
        for (Book book : _booksList) {
            List<Author> authorsList = authorDao.getBookAuthors(book);
            List<Genre> genresList = genreDao.getBookGenres(book);
            booksList.add(new Book(book.getId(), book.getName(), book.getIsbn(), authorsList, genresList));
        }

        return booksList;
    }

    @Override
    @Transactional
    public Book insert(Book book) {

        List<Author> authorsList = book.getAuthorsList();
        List<Genre> genresList = book.getGenresList();

        MapSqlParameterSource bookParameters = new MapSqlParameterSource();
        bookParameters.addValue("name", book.getName());
        bookParameters.addValue("isbn", book.getIsbn());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update("insert into book(name) values (:name)", bookParameters, keyHolder);

        long bookId = keyHolder.getKey().longValue();

        for (Author author : authorsList) {
            jdbc.update("insert into book_author(book_id, author_id) values (:bookId, :authorId)",
                    Map.of("bookId", bookId,
                            "authorId", author.getId())
            );
        }

        for (Genre genre : genresList) {
            jdbc.update("insert into book_genre(book_id, genre_id) values (:bookId, :genreId)",
                    Map.of("bookId", bookId,
                            "genreId", genre.getId())
            );
        }

        long id = keyHolder.getKey().longValue();

        return getById(id);
    }

    @Override
    @Transactional
    public Book update(Book book) {
        MapSqlParameterSource bookParameters = new MapSqlParameterSource();
        bookParameters.addValue("id", book.getId());
        bookParameters.addValue("name", book.getName());
        bookParameters.addValue("isbn", book.getIsbn());

        jdbc.update("update book set name = :name, isbn = :isbn where id = :id", bookParameters);

        long bookId = book.getId();

        jdbc.update("delete from book_author where book_id = :bookId", Map.of("bookId", bookId));
        jdbc.update("delete from book_genre where book_id = :bookId", Map.of("bookId", bookId));

        for (Author author : book.getAuthorsList()) {
            jdbc.update("insert into book_author(book_id, author_id) values (:bookId, :authorId)",
                    Map.of("bookId", bookId,
                            "authorId", author.getId())
            );
        }

        for (Genre genre : book.getGenresList()) {
            jdbc.update("insert into book_genre(book_id, genre_id) values (:bookId, :genreId)",
                    Map.of("bookId", bookId,
                            "genreId", genre.getId())
            );
        }

        return getById(book.getId());
    }

    @Override
    //@Transactional
    public int deleteById(long id) {
        jdbc.update("delete from book_author where book_id = :bookId", Map.of("bookId", id));
        jdbc.update("delete from book_genre where book_id = :bookId", Map.of("bookId", id));
        return jdbc.update("delete from book where id = :id", Map.of("id", id));
    }

    private static class BookMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String isbn = resultSet.getString("isbn");

            return new Book(id, name, isbn, null, null);
        }
    }
}
