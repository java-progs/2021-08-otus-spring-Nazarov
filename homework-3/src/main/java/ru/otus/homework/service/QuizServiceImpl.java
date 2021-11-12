package ru.otus.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Question;

import java.util.List;
import java.util.Locale;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuestionService questionService;
    private final IOService ioService;
    private final int answersForPassing;
    private final MessageSource messageSource;

    private Locale locale;

    {
        locale = Locale.getDefault();
    }

    public QuizServiceImpl(QuestionService questionService, IOService ioService,
                           @Value("${quiz.countAnswersForPassing}") int answersForPassing,
                           MessageSource messageSource) {
        this.questionService = questionService;
        this.ioService = ioService;
        this.answersForPassing = answersForPassing;
        this.messageSource = messageSource;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void startQuiz() {
        int correctAnswers;
        questionService.setLocale(locale);

        studentGreeting(questionService.getCountQuestions());

        correctAnswers = quizProcess(questionService.getAllQuestions());

        endQuiz(answersForPassing, correctAnswers);
    }

    private void studentGreeting(int questionsCount) {
        ioService.sendMessage(messageSource.getMessage("strings.getName", null, locale));
        String studentName = ioService.getMessage();
        ioService.sendMessage(messageSource.getMessage("strings.startQuiz",
                new String[] {studentName, String.format("%s%n", questionsCount)}, locale));
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
        String quizStatus = messageSource.getMessage("strings.quizNotPassed", null, locale);
        if (correctAnswers >= answersForPassing) {
            quizStatus = messageSource.getMessage("strings.quizPassed", null, locale);
        }
        ioService.sendMessage(messageSource.getMessage("strings.quizCompleted",
                new String[] {String.format("%s%n", correctAnswers)}, locale));
        ioService.sendMessage(quizStatus);
    }

    public void askQuestion(int questionNumber, Question question) {
        ioService.sendMessage(messageSource.getMessage("strings.question",
                new String[] {String.format("%d", questionNumber), String.format("%s%n", question.getQuestion())}, locale));
    }

    public boolean getAndCheckAnswer(Question question) {
        ioService.sendMessage(messageSource.getMessage("strings.getAnswer", null, locale));
        String answer = ioService.getMessage();
        return question.checkAnswer(answer);
    }
}
