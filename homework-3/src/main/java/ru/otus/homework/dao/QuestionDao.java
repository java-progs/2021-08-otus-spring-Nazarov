package ru.otus.homework.dao;

import ru.otus.homework.domain.Question;

import java.util.List;
import java.util.Locale;

public interface QuestionDao {

    List<Question> getAll();

    void setLocale(Locale locale);

    Locale getLocale();
}
