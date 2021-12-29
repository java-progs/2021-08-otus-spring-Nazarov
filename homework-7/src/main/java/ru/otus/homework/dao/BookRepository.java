package ru.otus.homework.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.homework.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
