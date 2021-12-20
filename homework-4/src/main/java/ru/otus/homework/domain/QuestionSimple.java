package ru.otus.homework.domain;

import java.util.Objects;

//Вопрос с вводом текстового ответа
public class QuestionSimple implements Question {

    private final String question;
    private final String correctAnswer;

    public QuestionSimple(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public boolean checkAnswer(String answer) {
        return answer.trim()
                .equalsIgnoreCase(correctAnswer.trim());
    }

    @Override
    public String toString() {
        return "QuestionSimple{" +
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

        QuestionSimple that = (QuestionSimple) o;

        return question.equals(that.question) &&
                correctAnswer.equals(that.correctAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, correctAnswer);
    }
}
