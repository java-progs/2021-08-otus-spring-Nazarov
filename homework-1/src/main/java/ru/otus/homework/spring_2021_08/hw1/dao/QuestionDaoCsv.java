package ru.otus.homework.spring_2021_08.hw1.dao;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.homework.spring_2021_08.hw1.domain.Question;
import ru.otus.homework.spring_2021_08.hw1.domain.QuestionChoiceAnswer;
import ru.otus.homework.spring_2021_08.hw1.domain.QuestionSimple;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoCsv implements QuestionDao {

    private final String csvResourcePath;
    private final char separator;
    private final boolean ignoreQuotations;
    private final int skipLines;

    public QuestionDaoCsv(String csvResourcePath, char separator, boolean ignoreQuotations, int skipLines) {
        this.csvResourcePath = csvResourcePath;
        this.separator = separator;
        this.ignoreQuotations = ignoreQuotations;
        this.skipLines = skipLines;
    }

    @Override
    public List<Question> getAll() {
        try {
            Resource csvResource = new ClassPathResource(this.csvResourcePath);
            InputStream inputStream = csvResource.getInputStream();
            Reader fileReader = new InputStreamReader(inputStream);

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(separator)
                    .withIgnoreQuotations(ignoreQuotations)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(skipLines)
                    .withCSVParser(parser)
                    .build();

            List<String[]> recordsList;
            recordsList = csvReader.readAll();
            fileReader.close();
            csvReader.close();

            List<Question> questionList = new ArrayList<>();
            for (String[] records : recordsList) {
                String questionValue = records[0];
                String answersListValue = records[1];
                String correctAnswerValue = records[2];

                //Если список вариантов ответа answersListValue пустой, то это простой вопрос с вводом ответа
                //Иначе это вопрос с выбором ответа
                if (answersListValue == null || answersListValue.equals("")) {
                    questionList.add(new QuestionSimple(questionValue, correctAnswerValue));
                } else {
                    try {
                        Integer.parseInt(correctAnswerValue);
                        questionList.add(new QuestionChoiceAnswer(questionValue + ' '
                                + answersListValue, correctAnswerValue));
                    } catch (NumberFormatException e) {
                        //пропуск вопроса
                    }
                }
            }

            return questionList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
