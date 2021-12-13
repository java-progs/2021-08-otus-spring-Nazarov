package ru.otus.homework.domain;

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
}
