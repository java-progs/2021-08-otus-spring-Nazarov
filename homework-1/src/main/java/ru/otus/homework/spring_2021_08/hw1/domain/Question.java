package ru.otus.homework.spring_2021_08.hw1.domain;

public interface Question {

    String getQuestion();

    boolean checkAnswer(String answer);
}
