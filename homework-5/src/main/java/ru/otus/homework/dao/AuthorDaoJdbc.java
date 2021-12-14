package ru.otus.homework.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.otus.homework.domain.Author;
import ru.otus.homework.domain.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class AuthorDaoJdbc implements AuthorDao {

    private final NamedParameterJdbcOperations jdbc;

    public AuthorDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int count() {
        return jdbc.getJdbcOperations().queryForObject("select count(*) from author", Integer.class);
    }

    @Override
    public Author getById(long id) {
        return jdbc.queryForObject("select a.id, a.surname, a.name, a.patronymic from author a where id = :id",
                Map.of("id", id), new AuthorMapper());
    }

    @Override
    public List<Author> getAll() {
        return jdbc.query("select a.id, a.surname, a.name, a.patronymic from author a", new AuthorMapper());
    }

    @Override
    public List<Author> getActiveAuthors() {
        return jdbc.query("select a.id, a.surname, a.name, a.patronymic from author a inner join book_author b " +
                "on a.id = b.author_id group by a.id", new AuthorMapper());
    }

    @Override
    public List<Author> getBookAuthors(Book book) {
        return jdbc.query("select a.id, a.surname, a.name, a.patronymic from author a, book_author b " +
                "where a.id = b.author_id and b.book_id = :book_id",
                Map.of("book_id", book.getId()), new AuthorMapper());
    }

    @Override
    public Author insert(Author author) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("surname", author.getSurname());
        parameters.addValue("name", author.getName());
        parameters.addValue("patronymic", author.getPatronymic());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update("insert into author(surname, name, patronymic) " +
                "values (:surname, :name, :patronymic)",
               parameters, keyHolder);

        long id = keyHolder.getKey().longValue();

        return getById(id);
    }

    @Override
    public Author update(Author author) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", author.getId());
        parameters.addValue("surname", author.getSurname());
        parameters.addValue("name", author.getName());
        parameters.addValue("patronymic", author.getPatronymic());

        jdbc.update("update author set surname = :surname, name = :name, " +
                        "patronymic = :patronymic where id = :id", parameters);

        return getById(author.getId());
    }

    @Override
    public int deleteById(long id) {
        return jdbc.update("delete from author where id = :id", Map.of("id", id));
    }

    private static class AuthorMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String surname = resultSet.getString("surname");
            String name = resultSet.getString("name");
            String patronymic = resultSet.getString("patronymic");

            return new Author(id, surname, name, patronymic);
        }
    }

}
