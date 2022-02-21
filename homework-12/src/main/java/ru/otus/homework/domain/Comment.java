package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private String id;

    private String author;

    private LocalDateTime time;

    private String text;

    public Comment(String author, String text) {
        this(author, null, text);
    }

    public Comment(String author, LocalDateTime time, String text) {
        this.author = author;
        this.time = time;
        this.text = text;
    }
}
