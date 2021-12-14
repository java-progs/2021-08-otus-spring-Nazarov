package ru.otus.homework.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.otus.homework.dao.ex.BookAuthorRelation;
import ru.otus.homework.dao.ex.BookGenreRelation;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        Book _book = jdbc.queryForObject("select b.id, b.name, b.isbn from book b where id = :id",
                Map.of("id", id), new BookMapper()
        );

        List<Author> authorsList = authorDao.getBookAuthors(_book);
        List<Genre> genresList = genreDao.getBookGenres(_book);
        book = new Book(_book.getId(), _book.getName(), _book.getIsbn(), authorsList, genresList);

        return book;
    }

    @Override
    public List<Book> getAll() {
        List<Book> booksList = jdbc.query("select b.id, b.name, b.isbn from book b", new BookMapper());

        List<Author> activeAuthors = authorDao.getActiveAuthors();
        List<Genre> activeGenres = genreDao.getActiveGenre();

        List<BookAuthorRelation> bookAuthorRelations = getAuthorRelations();
        List<BookGenreRelation> bookGenreRelations = getGenreRelations();

        return mergeBookInfo(booksList, bookAuthorRelations, bookGenreRelations, activeAuthors, activeGenres);
    }

    private List<BookAuthorRelation> getAuthorRelations() {
        return jdbc.query("select b.book_id, b.author_id from book_author b order by b.book_id, b.author_id",
                (rs, i) -> new BookAuthorRelation(rs.getLong(1), rs.getLong(2)));
    }

    private List<BookGenreRelation> getGenreRelations() {
        return jdbc.query("select b.book_id, b.genre_id from book_genre b order by b.book_id, b.genre_id",
                (rs, i) -> new BookGenreRelation(rs.getLong(1), rs.getLong(2)));
    }

    @Override
    public List<Book> getBooksByAuthor(long id) {
        List<Book> booksList = new ArrayList<>();
        List<Book> _booksList = jdbc.query("select b.id, b.name, b.isbn from book b inner join book_author a on b.id = a.book_id where a.author_id = :authorId",
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
        List<Book> _booksList = jdbc.query("select b.id, b.name, b.isbn from book b inner join book_genre g on b.id = g.book_id where g.genre_id = :genreId",
                Map.of("genreId", id), new BookMapper());

        for (Book book : _booksList) {
            List<Author> authorsList = authorDao.getBookAuthors(book);
            List<Genre> genresList = genreDao.getBookGenres(book);
            booksList.add(new Book(book.getId(), book.getName(), book.getIsbn(), authorsList, genresList));
        }

        return booksList;
    }

    @Override
    public Book insert(Book book) {

        List<Author> authorsList = book.getAuthorsList();
        List<Genre> genresList = book.getGenresList();

        MapSqlParameterSource bookParameters = new MapSqlParameterSource();
        bookParameters.addValue("name", book.getName());
        bookParameters.addValue("isbn", book.getIsbn());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update("insert into book(name) values (:name)", bookParameters, keyHolder);

        long bookId = keyHolder.getKey().longValue();

        SqlParameterSource[] authorBatch = SqlParameterSourceUtils
                .createBatch(authorsList.stream()
                        .map((author) -> new BookAuthorRelation(bookId, author.getId())).collect(Collectors.toList()));
        jdbc.batchUpdate("insert into book_author(book_id, author_id) values (:bookId, :authorId)",
                    authorBatch);

        SqlParameterSource[] genresBatch = SqlParameterSourceUtils
                .createBatch(genresList.stream()
                        .map((genre) -> new BookGenreRelation(bookId, genre.getId())).collect(Collectors.toList()));
        jdbc.batchUpdate("insert into book_genre(book_id, genre_id) values (:bookId, :genreId)",
                genresBatch);

        long id = keyHolder.getKey().longValue();

        return getById(id);
    }

    @Override
    public Book update(Book book) {
        MapSqlParameterSource bookParameters = new MapSqlParameterSource();
        bookParameters.addValue("id", book.getId());
        bookParameters.addValue("name", book.getName());
        bookParameters.addValue("isbn", book.getIsbn());

        jdbc.update("update book set name = :name, isbn = :isbn where id = :id", bookParameters);

        long bookId = book.getId();

        jdbc.update("delete from book_author where book_id = :bookId", Map.of("bookId", bookId));
        jdbc.update("delete from book_genre where book_id = :bookId", Map.of("bookId", bookId));

        SqlParameterSource[] authorBatch = SqlParameterSourceUtils
                .createBatch(book.getAuthorsList().stream()
                        .map((author) -> new BookAuthorRelation(bookId, author.getId())).collect(Collectors.toList()));
        jdbc.batchUpdate("insert into book_author(book_id, author_id) values (:bookId, :authorId)",
                authorBatch);

        SqlParameterSource[] genresBatch = SqlParameterSourceUtils
                .createBatch(book.getGenresList().stream()
                        .map((genre) -> new BookGenreRelation(bookId, genre.getId())).collect(Collectors.toList()));
        jdbc.batchUpdate("insert into book_genre(book_id, genre_id) values (:bookId, :genreId)",
                genresBatch);

        return getById(book.getId());
    }

    @Override
    public int deleteById(long id) {
        return jdbc.update("delete from book where id = :id", Map.of("id", id));
    }

    private static class BookMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String isbn = resultSet.getString("isbn");


            return new Book(id, name, isbn, new ArrayList<>(), new ArrayList<>());
        }
    }

    private List<Book> mergeBookInfo(List<Book> books, List<BookAuthorRelation> bookAuthorRelations,
                               List<BookGenreRelation> bookGenreRelations, List<Author> authors, List<Genre> genres) {

        Map<Long, List<Long>> bookAuthorsMap = bookAuthorRelations.stream()
                .collect(Collectors.groupingBy(BookAuthorRelation::getBookId,
                        Collectors.mapping(BookAuthorRelation::getAuthorId, Collectors.toList())));

        Map<Long, List<Long>> bookGenresMap = bookGenreRelations.stream()
                .collect(Collectors.groupingBy(BookGenreRelation::getBookId,
                        Collectors.mapping(BookGenreRelation::getGenreId, Collectors.toList())));

        Map<Long, Author> authorsMap = authors.stream().collect(Collectors.toMap(Author::getId, Function.identity()));
        Map<Long, Genre> genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));


        books.forEach((book) -> {
            long bookId = book.getId();

            bookAuthorsMap.get(bookId).forEach((authorId) -> {
                if (authorsMap.containsKey(authorId)) {
                    book.getAuthorsList().add(authorsMap.get(authorId));
                }
            });

            bookGenresMap.get(bookId).forEach((genreId) -> {
                if (genresMap.containsKey(genreId)) {
                    book.getGenresList().add(genresMap.get(genreId));
                }
            });

        });

        return books;
    }
}
