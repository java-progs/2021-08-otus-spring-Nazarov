package ru.otus.homework.domain;

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
}
