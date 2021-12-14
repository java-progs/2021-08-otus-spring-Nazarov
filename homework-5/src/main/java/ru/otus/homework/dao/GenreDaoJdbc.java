package ru.otus.homework.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class GenreDaoJdbc implements GenreDao {

    private final NamedParameterJdbcOperations jdbc;

    public GenreDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int count() {
        return jdbc.getJdbcOperations().queryForObject("select count(*) from genre", Integer.class);
    }

    @Override
    public Genre getById(long id) {
        return jdbc.queryForObject("select g.id, g.name from genre g where id = :id",
                Map.of("id", id), new GenreMapper());
    }

    @Override
    public List<Genre> getAll() {
        return jdbc.query("select g.id, g.name from genre g", new GenreMapper());
    }

    @Override
    public List<Genre> getActiveGenre() {
        return jdbc.query("select g.id, g.name from genre g inner join book_genre b on g.id = b.genre_id group by g.id",
                new GenreMapper());
    }

    @Override
    public List<Genre> getBookGenres(Book book) {
        return jdbc.query("select g.id, g.name from genre g, book_genre b " +
                        "where g.id = b.genre_id and b.book_id = :book_id",
                Map.of("book_id", book.getId()), new GenreMapper()
        );
    }

    @Override
    public Genre insert(Genre genre) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", genre.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update("insert into genre(name) values (:name)", parameters, keyHolder);

        long id = keyHolder.getKey().longValue();

        return getById(id);
    }

    @Override
    public Genre update(Genre genre) {
        jdbc.update("update genre set name = :name where id = :id",
                Map.of("id", genre.getId(),
                        "name", genre.getName())
        );

        return getById(genre.getId());
    }

    @Override
    public int deleteById(long id) {
        return jdbc.update("delete from genre where id = :id", Map.of("id", id));
    }

    private static class GenreMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");

            return new Genre(id, name);
        }
    }
}
