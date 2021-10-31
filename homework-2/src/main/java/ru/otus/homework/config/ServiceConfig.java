package ru.otus.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.service.*;

@Configuration
public class ServiceConfig {

    @Bean
    public QuestionServiceImpl questionService(QuestionDao dao) {
        return new QuestionServiceImpl(dao);
    }

    @Bean
    public IOService ioService() {
        return new IOServiceImpl();
    }

    @Bean
    public QuizServiceImpl quizService(QuestionService questionService, IOService ioService) {
        return new QuizServiceImpl(questionService, ioService);
    }
}
