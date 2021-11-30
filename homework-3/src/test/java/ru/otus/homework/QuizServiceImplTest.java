package ru.otus.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    private InputStream is;
    private OutputStream os;
    private IOService ioService;
    private QuestionService questionService;
    private MessageService messageService;
    private QuizServiceImpl quizService;

    @Autowired
    LocalizationService localizationService;

    @BeforeEach
    public void createServices() {
        is = new ByteArrayInputStream("5".getBytes());
        os = new ByteArrayOutputStream();
        ioService = new IOServiceImpl(is, os);
        questionService = mock(QuestionServiceImpl.class);
        messageService = new MessageServiceImpl(localizationService, ioService);
        quizService = new QuizServiceImpl(questionService, messageService, 5);
    }

    @Test
    @DisplayName("Корректно задает вопрос")
    public void askQuestion() {
        messageService.setLocale(new Locale("ru", "RU"));
        Question question = new QuestionSimple("2 + 3 =", "5");
        quizService.askQuestion(7, question);
        assertThat(os.toString()).isEqualTo(String.format("%s%n", "Вопрос 7: 2 + 3 ="));
    }

    @Test
    @DisplayName("Корректно проверяет ответ")
    public void getAndCheckAnswer() {
        messageService.setLocale(new Locale("ru", "RU"));
        Question question = new QuestionSimple("2 + 3 =", "5");
        assertThat(quizService.getAndCheckAnswer(question)).isTrue();
    }

}
