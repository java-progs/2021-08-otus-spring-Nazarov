package ru.otus.homework.spring_2021_08.hw1;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.homework.spring_2021_08.hw1.domain.Question;
import ru.otus.homework.spring_2021_08.hw1.service.QuestionService;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/application-context.xml");

        QuestionService service = context.getBean(QuestionService.class);

        System.out.printf("Total questions: %s%n", service.getCountQuestions());

        List<Question> questionsList = service.getAllQuestions();
        int questionNumber = 0;

        for (Question question: questionsList) {
            questionNumber++;
            System.out.printf("Question %d: %s%n", questionNumber, question.getQuestion());
        }
    }
}
