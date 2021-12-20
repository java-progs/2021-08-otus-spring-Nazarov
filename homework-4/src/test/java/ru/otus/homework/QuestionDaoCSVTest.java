package ru.otus.homework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.config.CSVConfig;
import ru.otus.homework.config.LocaleConfig;
import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.dao.QuestionDaoCsv;
import ru.otus.homework.provider.LocaleProvider;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("DAO с локализацией")
public class QuestionDaoCSVTest {

    @Configuration
    static class NestedTestConfiguration {
        @Bean
        CSVConfig csvConfig() {
            CSVConfig csvConfig = new CSVConfig();
            csvConfig.setResourcePath("./questions.csv");
            csvConfig.setSeparator(',');
            csvConfig.setIgnoreQuotations(false);
            csvConfig.setSkipLines(1);

            return csvConfig;
        }

        @Bean
        LocaleConfig localeConfig() {
            LocaleConfig localeConfig = new LocaleConfig();
            localeConfig.setLocale(new Locale("ru", "RU"));
            return localeConfig;
        }

        @Bean
        LocaleProvider localeProvider() {
            return new LocaleProvider(localeConfig());
        }
    }

    @Autowired
    private CSVConfig csvConfig;

    @Autowired
    private LocaleProvider localeProvider;

    @Test
    @DisplayName("Находит вопросы на русском")
    void getRuQuestions() {
        localeProvider.setLocale(new Locale("ru", "RU"));
        QuestionDao dao = new QuestionDaoCsv(csvConfig, localeProvider);
        assertThat(dao.getAll()).asList().isNotEmpty();
        assertThat(dao.getAll()).size().isEqualTo(5);
    }

    @Test
    @DisplayName("Не находит вопросы на немецком")
    void getDeQuestions() {
        localeProvider.setLocale(new Locale("de", "DE"));
        QuestionDao dao = new QuestionDaoCsv(csvConfig, localeProvider);
        assertThat(dao.getAll()).asList().isEmpty();
    }

    @Test
    @DisplayName("Находит вопросы на английском")
    void getEnQuestions() {
        localeProvider.setLocale(new Locale("en", "EN"));
        QuestionDao dao = new QuestionDaoCsv(csvConfig, localeProvider);
        assertThat(dao.getAll()).asList().isNotEmpty();
        assertThat(dao.getAll()).size().isEqualTo(4);
    }
}
