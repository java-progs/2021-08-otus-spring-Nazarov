package ru.otus.homework.spring_2021_08.hw1.service;

import ru.otus.homework.spring_2021_08.hw1.domain.Question;

import java.util.List;

public interface QuestionService {

    int getCountQuestions();

    List<Question> getAllQuestions();
}
