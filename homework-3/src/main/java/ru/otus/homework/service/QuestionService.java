package ru.otus.homework.service;

import ru.otus.homework.domain.Question;

import java.util.List;

public interface QuestionService {

    int getCountQuestions();

    List<Question> getAllQuestions();

}
