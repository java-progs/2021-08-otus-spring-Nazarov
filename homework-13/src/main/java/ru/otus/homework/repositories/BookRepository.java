package ru.otus.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.homework.domain.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM book b LEFT OUTER JOIN book_genre bg ON bg.book_id = b.id WHERE bg.genre_id = :genreId",
        nativeQuery = true)
    List<Book> findAllBookByGenre(@Param("genreId") long genreId);

    @Query(value = "SELECT * FROM book b LEFT OUTER JOIN book_author ba ON ba.book_id = b.id WHERE ba.author_id = :authorId",
        nativeQuery = true)
    List<Book> findAllBookByAuthor(@Param("authorId") long authorId);

}
