package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    @NonNull
    private String name;

    @Column(name = "isbn")
    private String isbn;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private List<Author> authorsList;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genresList;
}
