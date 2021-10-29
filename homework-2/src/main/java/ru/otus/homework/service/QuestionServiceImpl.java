package ru.otus.homework.service;

import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.domain.Question;

import java.util.List;

public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao dao;

    public QuestionServiceImpl(QuestionDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Question> getAllQuestions() {
        return dao.getAll();
    }

    @Override
    public int getCountQuestions() {
        List<Question> questionsList = getAllQuestions();

        if (questionsList != null) {
            return questionsList.size();
        }
        return 0;
    }
}
