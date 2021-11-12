package ru.otus.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.homework.config.CSVConfig;
import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.dao.QuestionDaoCsv;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DAO с локализацией")
public class QuestionDaoCSVTest {
    private CSVConfig csvConfig;

    @BeforeEach
    void createCSVConfig() {
        csvConfig = new CSVConfig();
        csvConfig.setResourcePath("./questions.csv");
        csvConfig.setSeparator(',');
        csvConfig.setIgnoreQuotations(false);
        csvConfig.setSkipLines(1);
    }

    @Test
    @DisplayName("Находит вопросы на русском")
    void getRuQuestions() {
        Locale locale = new Locale("ru", "RU");
        QuestionDao dao = new QuestionDaoCsv(csvConfig);
        dao.setLocale(locale);
        assertThat(dao.getAll()).asList().isNotEmpty();
        assertThat(dao.getAll()).size().isEqualTo(5);
    }

    @Test
    @DisplayName("Не находит вопросы на немецком")
    void getDeQuestions() {
        Locale locale = new Locale("de", "DE");
        QuestionDao dao = new QuestionDaoCsv(csvConfig);
        dao.setLocale(locale);
        assertThat(dao.getAll()).asList().isEmpty();
    }

    @Test
    @DisplayName("Находит вопросы на английском")
    void getEnQuestions() {
        Locale locale = new Locale("en", "EN");
        QuestionDao dao = new QuestionDaoCsv(csvConfig);
        dao.setLocale(locale);
        assertThat(dao.getAll()).asList().isNotEmpty();
        assertThat(dao.getAll()).size().isEqualTo(4);
    }
}
