package ru.otus.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.domain.Question;
import ru.otus.homework.domain.QuestionChoiceAnswer;
import ru.otus.homework.domain.QuestionSimple;
import ru.otus.homework.service.QuestionServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Question service")
class QuestionServiceImplTest {

    private QuestionDao questionDao;
    private QuestionServiceImpl questionService;

    @BeforeEach
    void createQuestionService() {
        questionDao = mock(QuestionDao.class);
        questionService = new QuestionServiceImpl(questionDao);
    }

    @DisplayName("Корректно получает список вопросов")
    @Test
    void getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new QuestionSimple("7 - 2 = ", "5"));
        questionList.add(new QuestionChoiceAnswer("What is the List? 1 - interface, 2 - abstract class, " +
                "3 - functional interface", "1"));

        given(questionDao.getAll()).willReturn(questionList);
        assertThat(questionService.getAllQuestions()).isNotEmpty().isEqualTo(questionList);
    }

    @DisplayName("Корректно получает количество вопросов")
    @Test
    void getNextQuestion() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new QuestionSimple("10 ^ 2 = ", "100"));
        questionList.add(new QuestionSimple("200 - 2 = ", "198"));
        questionList.add(new QuestionSimple("Spring is a framework?", "yes"));

        when(questionDao.getAll()).thenReturn(questionList);
        assertThat(questionService.getCountQuestions()).isEqualTo(questionList.size());
    }
}