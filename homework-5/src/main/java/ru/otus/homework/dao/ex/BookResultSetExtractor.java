package ru.otus.homework.dao.ex;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class BookResultSetExtractor implements ResultSetExtractor<Map<Long, Book>> {

    @Override
    public Map<Long, Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Book> books = new HashMap<>();

        while (rs.next()) {
            long id = rs.getLong("book_id");
            Book book = books.get(id);

            Set<Author> authorsSet = new HashSet<>();
            Set<Genre> genresSet = new HashSet<>();

            addAuthorRecord(authorsSet, rs);
            addGenreRecord(genresSet, rs);

            if(book == null) {
                book = new Book(id, rs.getString("book_name"), rs.getString("book_isbn"),
                        authorsSet.stream().collect(Collectors.toList()),
                        genresSet.stream().collect(Collectors.toList()));
                books.put(id, book);
            } else {
                authorsSet = book.getAuthorsList().stream().collect(Collectors.toSet());
                genresSet = book.getGenresList().stream().collect(Collectors.toSet());

                addAuthorRecord(authorsSet, rs);
                addGenreRecord(genresSet, rs);

                Book updatedBook = new Book(id, book.getName(), book.getIsbn(),
                        authorsSet.stream().collect(Collectors.toList()),
                        genresSet.stream().collect(Collectors.toList()));
                books.put(id, updatedBook);
            }
        }

        if (books.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return books;
    }

    private void addAuthorRecord(Set<Author> set, ResultSet rs) throws SQLException {
        if (rs.getLong("author_id") != 0) {
            set.add(new Author(rs.getLong("author_id"), rs.getString("author_surname"),
                    rs.getString("author_name"), rs.getString("author_patronymic")));
        }
    }

    private void addGenreRecord(Set<Genre> set, ResultSet rs) throws SQLException {
        if (rs.getLong("genre_id") != 0) {
            set.add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
        }
    }
}
