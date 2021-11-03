package ru.otus.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Question;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuestionService questionService;
    private final IOService ioService;
    private final int answersForPassing;

    public QuizServiceImpl(QuestionService questionService, IOService ioService,
                           @Value("${quiz.countAnswersForPassing}") int answersForPassing) {
        this.questionService = questionService;
        this.ioService = ioService;
        this.answersForPassing = answersForPassing;
    }

    @Override
    public void startQuiz() {
        int correctAnswers;

        studentGreeting(questionService.getCountQuestions());

        correctAnswers = quizProcess(questionService.getAllQuestions());

        endQuiz(answersForPassing, correctAnswers);
    }

    private void studentGreeting(int questionsCount) {
        ioService.sendMessage("Enter your name: ");
        String studentName = ioService.getMessage();
        ioService.sendMessage(String.format("Hello, %s! A quiz has started. Total questions: %s%n", studentName, questionsCount));
    }

    private int quizProcess(List<Question> questionsList) {
        int correctAnswers = 0;

        for (int i = 0; i < questionsList.size(); i++) {
            Question question = questionsList.get(i);
            askQuestion(i + 1, question);

            if (getAndCheckAnswer(question)) {
                correctAnswers++;
            }
        }

        return correctAnswers;
    }

    private void endQuiz(int answersForPassing, int correctAnswers) {
        String quizStatus = "not passed";
        if (correctAnswers >= answersForPassing) {
            quizStatus = "passed";
        }
        ioService.sendMessage(String.format("The Quiz is completed. Correct answers: %s%n", correctAnswers));
        ioService.sendMessage(String.format("The Quiz is %s%n", quizStatus));
    }

    public void askQuestion(int questionNumber, Question question) {
        ioService.sendMessage(String.format("Question %d: %s%n", questionNumber, question.getQuestion()));
    }

    public boolean getAndCheckAnswer(Question question) {
        ioService.sendMessage("Your answer: ");
        String answer = ioService.getMessage();
        return question.checkAnswer(answer);
    }
}
