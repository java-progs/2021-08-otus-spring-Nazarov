package ru.otus.homework.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.homework.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}