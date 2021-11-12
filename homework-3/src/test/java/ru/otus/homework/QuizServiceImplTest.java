package ru.otus.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import ru.otus.homework.domain.Question;
import ru.otus.homework.domain.QuestionSimple;
import ru.otus.homework.service.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
@DisplayName("Quiz service")
public class QuizServiceImplTest {
    
    private QuestionService questionService;
    private IOService ioService;
    private QuizServiceImpl quizService;

    @Autowired
    private MessageSource messageSource;

    @BeforeEach
    void createServices() {
        questionService = mock(QuestionServiceImpl.class);
    }
    
    @DisplayName("Корректно задается вопрос на английском")
    @Test
    void askQuestionEn() {
        InputStream is = new ByteArrayInputStream("".getBytes());
        OutputStream os = new ByteArrayOutputStream();

        ioService = new IOServiceImpl(is, os);
        quizService = new QuizServiceImpl(questionService, ioService, 1, messageSource);
        quizService.setLocale(new Locale("en" , "EN"));

        Question questionSimple = new QuestionSimple("10 * 2 = ", "20");
        quizService.askQuestion(2, questionSimple);

        assertThat(os.toString()).isEqualTo(String.format("Question %s: %s%n", 2, questionSimple.getQuestion()));
    }

    @DisplayName("Корректно проверяется ответ на английском")
    @Test
    void getAndCheckAnswerEn() {
        InputStream is = new ByteArrayInputStream("20\n".getBytes());
        OutputStream os = new ByteArrayOutputStream();

        ioService = new IOServiceImpl(is, os);
        quizService = new QuizServiceImpl(questionService, ioService, 1, messageSource);
        quizService.setLocale(new Locale("en" , "EN"));

        Question questionSimple = new QuestionSimple("10 * 2 = ", "20");

        assertThat(quizService.getAndCheckAnswer(questionSimple)).isTrue();
        assertThat(os.toString()).isEqualTo("Your answer: ");
    }

    @DisplayName("Корректно задается вопрос на русском")
    @Test
    void askQuestionRu() {
        InputStream is = new ByteArrayInputStream("".getBytes());
        OutputStream os = new ByteArrayOutputStream();

        ioService = new IOServiceImpl(is, os);
        quizService = new QuizServiceImpl(questionService, ioService, 1, messageSource);
        quizService.setLocale(new Locale("ru" , "RU"));

        Question questionSimple = new QuestionSimple("10 * 2 = ", "20");
        quizService.askQuestion(2, questionSimple);

        assertThat(os.toString()).isEqualTo(String.format("Вопрос %s: %s%n", 2, questionSimple.getQuestion()));
    }

    @DisplayName("Корректно проверяется ответ на русском")
    @Test
    void getAndCheckAnswerRu() {
        InputStream is = new ByteArrayInputStream("20\n".getBytes());
        OutputStream os = new ByteArrayOutputStream();

        ioService = new IOServiceImpl(is, os);
        quizService = new QuizServiceImpl(questionService, ioService, 1, messageSource);
        quizService.setLocale(new Locale("ru" , "RU"));

        Question questionSimple = new QuestionSimple("10 * 2 = ", "20");

        assertThat(quizService.getAndCheckAnswer(questionSimple)).isTrue();
        assertThat(os.toString()).isEqualTo("Ответ: ");
    }
}
