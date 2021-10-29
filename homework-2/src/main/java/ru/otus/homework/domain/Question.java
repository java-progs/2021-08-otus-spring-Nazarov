package ru.otus.homework.domain;

public interface Question {

    String getQuestion();

    boolean checkAnswer(String answer);
}
