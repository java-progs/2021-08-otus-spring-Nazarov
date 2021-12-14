package ru.otus.homework.dao.ex;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookAuthorRelation {
    private long bookId;
    private long authorId;
}
