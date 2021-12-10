package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Author {
    private long id;
    @NonNull
    private final String surname;
    @NonNull
    private final String name;
    private final String patronymic;


}
