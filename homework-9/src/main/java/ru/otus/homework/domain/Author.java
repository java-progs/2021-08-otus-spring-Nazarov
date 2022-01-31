package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Author {

    @Id
    private String id;

    private String surname;

    private String name;

    private String patronymic;

    public Author(String surname, String name, String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    public String getFullName() {
        var fullName = String.format("%s %s", surname, name);
        if (!(patronymic == null) && patronymic.length() > 0 ) {
            fullName += " " + patronymic;
        }

        return fullName;
    }
}
