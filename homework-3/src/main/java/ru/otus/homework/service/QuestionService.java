package ru.otus.homework.service;

import ru.otus.homework.domain.Question;

import java.util.List;
import java.util.Locale;

public interface QuestionService {

    int getCountQuestions();

    List<Question> getAllQuestions();

    void setLocale(Locale locale);
}
