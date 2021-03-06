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
import ru.otus.homework.domain.Question;
import ru.otus.homework.domain.QuestionChoiceAnswer;
import ru.otus.homework.domain.QuestionSimple;
import ru.otus.homework.provider.LocaleProvider;
import ru.otus.homework.service.QuestionService;
import ru.otus.homework.service.QuestionServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Question service")
class QuestionServiceImplTest {


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

        @Bean
        QuestionDao questionDao() {
            return new QuestionDaoCsv(csvConfig(), localeProvider());
        }

        @Bean
        QuestionService questionService() {
            return new QuestionServiceImpl(questionDao());
        }
    }

    @Autowired
    private LocaleConfig localeConfig;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionServiceImpl questionService;

    @DisplayName("?????????????????? ???????????????? ???????????? ????????????????")
    @Test
    void getAllQuestions() {
        localeConfig.setLocale(new Locale("ru", "RU"));
        List<Question> questionListRu = new ArrayList<>();
        questionListRu.add(new QuestionSimple("2 + 3 =", "5"));
        questionListRu.add(new QuestionSimple("List ?????? ???????????????????", "????"));
        questionListRu.add(new QuestionChoiceAnswer("?????? ?????????? Maven? 1-java ????????????????????, 2-???????????????????? ???????????????????? ??????????????????????????, 3-?????????????? ???????????? java","2"));

        List<Question> questionListEn = new ArrayList<>();
        questionListEn.add(new QuestionSimple("A List is a interface?", "yes"));

        assertThat(questionService.getAllQuestions()).isNotEmpty();
        assertThat(questionService.getAllQuestions()).containsAll(questionListRu);
        assertThat(questionService.getAllQuestions()).doesNotContainAnyElementsOf(questionListEn);
    }

    @DisplayName("?????????????????? ???????????????? ???????????????????? ????????????????")
    @Test
    void getNextQuestion() {
        assertThat(questionService.getCountQuestions()).isEqualTo(5);
    }
}