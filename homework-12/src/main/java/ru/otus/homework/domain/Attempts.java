package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document
public class Attempts {

    @Id
    private String id;
    private String username;
    private int attempts;
}
