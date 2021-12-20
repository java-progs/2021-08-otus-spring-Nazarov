package ru.otus.homework.domain;

import java.util.Objects;

public class QuestionChoiceAnswer implements Question {

    private final String question;
    private final String correctAnswer;

    public QuestionChoiceAnswer(String question, String answer) {
        this.question = question;
        this.correctAnswer = answer;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public boolean checkAnswer(String answer) {
        try {
            int userAnswer = Integer.parseInt(answer.trim());
            return Integer.parseInt(correctAnswer.trim()) == userAnswer;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "QuestionChoiceAnswer{" +
                "question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuestionChoiceAnswer that = (QuestionChoiceAnswer) o;

        return question.equals(that.question) &&
                correctAnswer.equals(that.correctAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, correctAnswer);
    }
}
