package ru.otus.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Question;

import java.util.List;
import java.util.Locale;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuestionService questionService;
    private final MessageService messageService;
    private final int answersForPassing;

    public QuizServiceImpl(QuestionService questionService, MessageService messageService,
                           @Value("${quiz.countAnswersForPassing}") int answersForPassing) {
        this.questionService = questionService;
        this.messageService = messageService;
        this.answersForPassing = answersForPassing;
    }

    @Override
    public void startQuiz() {
        int correctAnswers;

        messageService.setLocale(new Locale("ru", "RU"));

        studentGreeting(questionService.getCountQuestions());

        correctAnswers = quizProcess(questionService.getAllQuestions());

        endQuiz(answersForPassing, correctAnswers);
    }

    private void studentGreeting(int questionsCount) {
        messageService.showMessage("strings.getName", null);
        String studentName = messageService.readMessage();
        messageService.showMessage("strings.startQuiz",
                new String[] {studentName, String.format("%s%n", questionsCount)});
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
        messageService.showMessage("strings.quizCompleted",
                new String[] {String.format("%s%n%s", correctAnswers,
                        getQuizStatus(answersForPassing, correctAnswers))});
    }

    private String getQuizStatus(int answersForPassing, int correctAnswers) {
        if (correctAnswers >= answersForPassing) {
            return messageService.getMessage("strings.quizPassed", null);
        }

        return messageService.getMessage("strings.quizNotPassed", null);
    }

    public void askQuestion(int questionNumber, Question question) {
        messageService.showMessage("strings.question",
                new String[] {String.format("%d", questionNumber), String.format("%s%n", question.getQuestion())});
    }

    public boolean getAndCheckAnswer(Question question) {
        messageService.showMessage("strings.getAnswer", null);
        return question.checkAnswer(messageService.readMessage());
    }
}
