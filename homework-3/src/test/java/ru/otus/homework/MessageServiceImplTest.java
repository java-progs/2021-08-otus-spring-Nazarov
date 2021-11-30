package ru.otus.homework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.homework.service.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Message service")
public class MessageServiceImplTest {

    private OutputStream os;
    private InputStream is;
    private MessageService messageService;
    private IOService ioService;

    @Autowired
    private LocalizationService localizationService;

    @Test
    @DisplayName("Корректно выводит сообщение")
    public void showMessage() {
        is = new ByteArrayInputStream("".getBytes());
        os = new ByteArrayOutputStream();
        ioService = new IOServiceImpl(is, os);

        messageService = new MessageServiceImpl(localizationService, ioService);

        messageService.showMessage("strings.quizPassed", null);
        assertThat(os.toString()).isEqualTo("Тестирование пройдено");
    }

    @Test
    @DisplayName("Корректно получает ответ")
    public void readMessage() {
        is = new ByteArrayInputStream("Answer text\n".getBytes());
        os = new ByteArrayOutputStream();
        ioService = new IOServiceImpl(is, os);

        messageService = new MessageServiceImpl(localizationService, ioService);

        assertThat(messageService.readMessage()).isEqualTo("Answer text");
    }

    @Test
    @DisplayName("Корректно получает сообщение")
    public void getMessage() {
        messageService = new MessageServiceImpl(localizationService, ioService);
        messageService.setLocale(new Locale("ru", "RU"));
        assertThat(messageService.getMessage("strings.quizCompleted", new String[] {"5"}))
                .isEqualTo("Тестирование завершено. Правильных ответов: 5");
    }

}