package ru.otus.homework.service;

import ru.otus.homework.domain.Question;

import java.util.List;

public class QuizServiceImpl implements QuizService {

    private final QuestionService questionService;
    private final IOService ioService;

    private List<Question> questionsList;
    private int questionsCount;
    private int nextQuestionId;

    public QuizServiceImpl(QuestionService questionService, IOService ioService) {
        this.questionService = questionService;
        this.ioService = ioService;
    }

    @Override
    public void startQuiz() {
        questionsList = questionService.getAllQuestions();
        questionsCount = questionService.getCountQuestions();
        nextQuestionId = 0;
        ioService.sendMessage(String.format("A quiz has started. Total questions: %s%n", questionsCount));

        while (nextQuestionId < questionsCount) {
            askNextQuestion();
            nextQuestionId++;
        }
    }

    private void askNextQuestion() {
        ioService.sendMessage(String.format("Question %d: %s%n", nextQuestionId + 1,
                questionsList.get(nextQuestionId).getQuestion()));
    }
}
