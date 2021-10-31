package ru.otus.homework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.dao.QuestionDaoCsv;

@PropertySource("classpath:application.properties")
@Configuration
public class DaoConfig {

    @Value("${csv.resourcePath}")
    private String csvResourcePath;

    @Value("${csv.separator}")
    private char separator;

    @Value("${csv.ignoreQuotations}")
    private boolean ignoreQuotations;

    @Value("${csv.skipLines}")
    private int skipLines;

    @Bean
    public QuestionDao questionDao() {
        return new QuestionDaoCsv(csvResourcePath, separator, ignoreQuotations, skipLines);
    }

}
