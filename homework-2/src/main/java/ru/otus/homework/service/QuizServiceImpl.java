package ru.otus.homework.service;

import org.springframework.beans.factory.annotation.Value;
import ru.otus.homework.domain.Question;

import java.util.List;

public class QuizServiceImpl implements QuizService {

    private final QuestionService questionService;
    private final IOService ioService;

    private List<Question> questionsList;
    private int questionsCount;
    private int nextQuestionId;

    @Value("${quiz.countAnswersForPassing}")
    private int answersForPassing;
    private int correctAnswers;
    private String studentName;

    public QuizServiceImpl(QuestionService questionService, IOService ioService) {
        this.questionService = questionService;
        this.ioService = ioService;
    }

    @Override
    public void startQuiz() {
        questionsList = questionService.getAllQuestions();
        questionsCount = questionService.getCountQuestions();

        nextQuestionId = 0;
        correctAnswers = 0;
        ioService.sendMessage("Enter your name: ");
        studentName = ioService.getMessage();
        ioService.sendMessage(String.format("Hello, %s! A quiz has started. Total questions: %s%n", studentName, questionsCount));

        while (nextQuestionId < questionsCount) {
            askNextQuestion();
            if (getAndCheckAnswer()) correctAnswers++;
            nextQuestionId++;
        }

        endQuiz();
    }

    private void endQuiz() {
        String quizStatus = "not passed";
        if (correctAnswers >= answersForPassing) {
            quizStatus = "passed";
        }
        ioService.sendMessage(String.format("The Quiz is completed. Correct answers: %s%n", correctAnswers));
        ioService.sendMessage(String.format("The Quiz is %s%n", quizStatus));
    }

    private void askNextQuestion() {
        ioService.sendMessage(String.format("Question %d: %s%n", nextQuestionId + 1,
                questionsList.get(nextQuestionId).getQuestion()));
    }

    private boolean getAndCheckAnswer() {
        ioService.sendMessage("Your answer: ");
        String answer = ioService.getMessage();
        return questionsList.get(nextQuestionId).checkAnswer(answer);
    }
}
