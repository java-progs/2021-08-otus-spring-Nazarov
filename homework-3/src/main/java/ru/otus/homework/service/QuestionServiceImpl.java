package ru.otus.homework.service;

import org.springframework.stereotype.Service;
import ru.otus.homework.dao.QuestionDao;
import ru.otus.homework.domain.Question;

import java.util.List;
import java.util.Locale;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao dao;

    public QuestionServiceImpl(QuestionDao dao) {
        this.dao = dao;
    }

    public void setLocale(Locale locale) {
        dao.setLocale(locale);
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
