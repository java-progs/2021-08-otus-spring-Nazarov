package ru.otus.homework.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.homework.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
