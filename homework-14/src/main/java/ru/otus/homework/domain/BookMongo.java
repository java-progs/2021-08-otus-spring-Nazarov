package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "book")
public class BookMongo {

    @Id
    private String id;

    private String name;

    private String isbn;

    @DBRef
    private List<AuthorMongo> authorsList;

    @DBRef
    private List<GenreMongo> genresList;

    private List<CommentMongo> commentsList;

}
