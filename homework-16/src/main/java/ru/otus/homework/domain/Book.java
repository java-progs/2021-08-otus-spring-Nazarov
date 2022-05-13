package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Book {

    @Id
    private String id;

    private String name;

    private String isbn;

    @DBRef
    private List<Author> authorsList;

    @DBRef
    private List<Genre> genresList;

    private List<Comment> commentsList;

    public Book(String name, String isbn, List<Author> authorsList, List<Genre> genresList, List<Comment> commentsList) {
        this.name = name;
        this.isbn = isbn;
        this.authorsList = authorsList;
        this.genresList = genresList;
        this.commentsList = commentsList;
    }
}
