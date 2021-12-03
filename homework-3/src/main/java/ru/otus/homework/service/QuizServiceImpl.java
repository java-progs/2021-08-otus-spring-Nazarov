package ru.otus.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Question;
import ru.otus.homework.provider.LocaleProvider;

import java.util.List;
import java.util.Locale;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuestionService questionService;
    private final MessageService messageService;
    private final int answersForPassing;
    private final LocaleProvider localeProvider;

    public QuizServiceImpl(QuestionService questionService, MessageService messageService,
                           @Value("${quiz.countAnswersForPassing}") int answersForPassing,
                           LocaleProvider localeProvider) {
        this.questionService = questionService;
        this.messageService = messageService;
        this.answersForPassing = answersForPassing;
        this.localeProvider = localeProvider;
    }

    @Override
    public void startQuiz() {
        int correctAnswers;

        selectLanguage();

        studentGreeting(questionService.getCountQuestions());

        correctAnswers = quizProcess(questionService.getAllQuestions());

        endQuiz(answersForPassing, correctAnswers);
    }

    private void selectLanguage() {
        Locale locale;

        messageService.showMessage("strings.choiceLanguage", String.format("%n"));
        messageService.showMessage("strings.enLanguage", String.format("%d", 1),
                String.format("%n"));
        messageService.showMessage("strings.ruLanguage", String.format("%d", 2),
                String.format("%n"));
        messageService.showMessage("strings.yourChoice", null);
        String choice = messageService.readMessage();

        try {
            int numChoice = Integer.parseInt(choice);
            switch (numChoice) {
                case 1:
                    locale = new Locale("en", "EN");
                    break;
                case 2:
                    locale = new Locale("ru", "RU");
                    break;
                default:
                    locale = localeProvider.getDefaultLocale();
            }
        } catch (NumberFormatException e) {
            locale = localeProvider.getDefaultLocale();
        }

        localeProvider.setLocale(locale);
    }

    private void studentGreeting(int questionsCount) {
        messageService.showMessage("strings.getName", null);
        String studentName = messageService.readMessage();
        messageService.showMessage("strings.startQuiz",
                studentName, String.format("%s%n", questionsCount));
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
                String.format("%s%n%s", correctAnswers,
                        getQuizStatus(answersForPassing, correctAnswers)));
    }

    private String getQuizStatus(int answersForPassing, int correctAnswers) {
        if (correctAnswers >= answersForPassing) {
            return messageService.getMessage("strings.quizPassed", null);
        }

        return messageService.getMessage("strings.quizNotPassed", null);
    }

    public void askQuestion(int questionNumber, Question question) {
        messageService.showMessage("strings.question",
                String.format("%d", questionNumber), String.format("%s%n", question.getQuestion()));
    }

    public boolean getAndCheckAnswer(Question question) {
        messageService.showMessage("strings.getAnswer", null);
        return question.checkAnswer(messageService.readMessage());
    }
}
