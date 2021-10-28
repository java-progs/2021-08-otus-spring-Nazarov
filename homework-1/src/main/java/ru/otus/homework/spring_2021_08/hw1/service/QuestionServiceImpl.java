package ru.otus.homework.spring_2021_08.hw1.service;

import ru.otus.homework.spring_2021_08.hw1.dao.QuestionDao;
import ru.otus.homework.spring_2021_08.hw1.domain.Question;

import java.util.List;

public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao dao;
    private List<Question> questionsList;

    public QuestionServiceImpl(QuestionDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Question> getAllQuestions() {
        return dao.getAll();
    }

    @Override
    public int getCountQuestions() {
        questionsList = getAllQuestions();

        if (questionsList != null) {
            return questionsList.size();
        }
        return 0;
    }
}
