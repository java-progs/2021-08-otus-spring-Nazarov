package ru.otus.homework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.homework.provider.LocaleProvider;
import ru.otus.homework.service.LocalizationService;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Localization service")
public class LocalizationServiceImplTest {

    @Autowired
    private LocaleProvider localeProvider;

    @Autowired
    private LocalizationService localizationService;

    @DisplayName("Корректно выводит сообщение на русском")
    @Test
    void showRuMessage() {
        localeProvider.setLocale(new Locale("ru", "RU"));
        assertThat(localizationService.getLocalizationMessage("strings.quizPassed", null))
                .isEqualTo("Тестирование пройдено");
    }

    @DisplayName("Корректно выводит сообщение на английском")
    @Test
    void showEnMessage() {
        localeProvider.setLocale(new Locale("en", "EN"));
        assertThat(localizationService.getLocalizationMessage("strings.question",
                String.format("%d", 1), String.format("%s", "question text")))
                .isEqualTo("Question 1: question text");
    }
}
