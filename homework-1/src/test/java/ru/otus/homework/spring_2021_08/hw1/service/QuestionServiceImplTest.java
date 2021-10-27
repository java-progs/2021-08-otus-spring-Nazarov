package ru.otus.homework.spring_2021_08.hw1.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.homework.spring_2021_08.hw1.dao.QuestionDao;
import ru.otus.homework.spring_2021_08.hw1.domain.Question;
import ru.otus.homework.spring_2021_08.hw1.domain.QuestionChoiceAnswer;
import ru.otus.homework.spring_2021_08.hw1.domain.QuestionSimple;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Question service")
class QuestionServiceImplTest {

    @DisplayName("Корректно получает список вопросов")
    @Test
    void getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new QuestionSimple("7 - 2 = ", "5"));
        questionList.add(new QuestionChoiceAnswer("What is the List? 1 - interface, 2 - abstract class, " +
                "3 - functional interface", "1"));

        QuestionDao questionDao = mock(QuestionDao.class);
        given(questionDao.getAll()).willReturn(questionList);

        QuestionServiceImpl questionService = new QuestionServiceImpl(questionDao);

        assertThat(questionService.getAllQuestions()).isNotEmpty().isEqualTo(questionList);
    }

    @DisplayName("Корректно получает количество вопросов")
    @Test
    void getNextQuestion() {
        List<Question> questionList = new ArrayList();
        questionList.add(new QuestionSimple("10 ^ 2 = ", "100"));
        questionList.add(new QuestionSimple("200 - 2 = ", "198"));
        questionList.add(new QuestionSimple("Spring is a framework?", "yes"));

        QuestionDao questionDao = mock(QuestionDao.class);
        QuestionServiceImpl questionService = new QuestionServiceImpl(questionDao);

        when(questionDao.getAll()).thenReturn(questionList);
        assertThat(questionService.getCountQuestions()).isEqualTo(questionList.size());
    }
}