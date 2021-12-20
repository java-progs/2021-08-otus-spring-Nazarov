package ru.otus.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.otus.homework.service.QuizService;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(Main.class, args);

		QuizService quizService = ctx.getBean(QuizService.class);
		quizService.startQuiz();
	}

}
