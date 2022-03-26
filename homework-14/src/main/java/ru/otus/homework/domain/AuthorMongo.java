package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "author")
public class AuthorMongo {

    @Id
    private String id;

    private Long oldId;

    private String surname;

    private String name;

    private String patronymic;

}
