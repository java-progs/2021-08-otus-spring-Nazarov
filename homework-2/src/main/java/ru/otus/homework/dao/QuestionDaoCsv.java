package ru.otus.homework.dao;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.otus.homework.domain.Question;
import ru.otus.homework.domain.QuestionChoiceAnswer;
import ru.otus.homework.domain.QuestionSimple;
import ru.otus.homework.exception.QuestionFormatException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionDaoCsv implements QuestionDao {

    private final String csvResourcePath;
    private final char separator;
    private final boolean ignoreQuotations;
    private final int skipLines;

    public QuestionDaoCsv(@Value("${csv.resourcePath}") String csvResourcePath,
                          @Value("${csv.separator}") char separator,
                          @Value("${csv.ignoreQuotations}") boolean ignoreQuotations,
                          @Value("${csv.skipLines}") int skipLines) {
        this.csvResourcePath = csvResourcePath;
        this.separator = separator;
        this.ignoreQuotations = ignoreQuotations;
        this.skipLines = skipLines;
    }

    private List<String[]> readCsv(String resourcePath) {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .withIgnoreQuotations(ignoreQuotations)
                .build();

        Resource csvResource = new ClassPathResource(resourcePath);

        try (Reader fileReader = new InputStreamReader(csvResource.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(fileReader)
                     .withSkipLines(skipLines)
                     .withCSVParser(parser)
                     .build()) {

            List<String[]> recordsList = csvReader.readAll();

            return recordsList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Question> createQuestionsList(List<String[]> recordsList) throws QuestionFormatException {
        List<Question> questionList = new ArrayList<>();

        for (String[] records : recordsList) {
            try {
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
                        throw new QuestionFormatException(String.format("Not number answer value: %s",
                                correctAnswerValue));
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new QuestionFormatException(String.format("Invalid answer format. Array index out of bounds: %s",
                        e.getMessage()));
            }
        }

        return questionList;
    }

    @Override
    public List<Question> getAll() {
        try {
            return createQuestionsList(readCsv(this.csvResourcePath));
        } catch (QuestionFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
