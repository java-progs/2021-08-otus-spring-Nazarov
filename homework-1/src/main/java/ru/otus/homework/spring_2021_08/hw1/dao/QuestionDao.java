package ru.otus.homework.spring_2021_08.hw1.dao;

import ru.otus.homework.spring_2021_08.hw1.domain.Question;

import java.util.List;

public interface QuestionDao {

    List<Question> getAll();
}
