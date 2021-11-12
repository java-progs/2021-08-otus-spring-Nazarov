package ru.otus.homework.dao;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.otus.homework.config.CSVConfig;
import ru.otus.homework.domain.Question;
import ru.otus.homework.domain.QuestionChoiceAnswer;
import ru.otus.homework.domain.QuestionSimple;
import ru.otus.homework.exception.QuestionCSVFormatException;
import ru.otus.homework.exception.QuestionFormatException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class QuestionDaoCsv implements QuestionDao {
    private final Locale DEFAULT_LOCALE = new Locale("en", "EN");

    private final String csvResourcePath;
    private final char separator;
    private final boolean ignoreQuotations;
    private final int skipLines;

    private Locale locale;

    public QuestionDaoCsv(CSVConfig config) {
        this.csvResourcePath = config.getResourcePath();
        this.separator = config.getSeparator();
        this.ignoreQuotations = config.isIgnoreQuotations();
        this.skipLines = config.getSkipLines();
        this.locale = DEFAULT_LOCALE;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
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
            return csvReader.readAll();
        } catch (IOException|CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Question> createQuestionsList(List<String[]> recordsList, Locale locale) throws QuestionFormatException,
            QuestionCSVFormatException {
        List<Question> questionList = new ArrayList<>();
        String localeName = locale.toString();
        List<String[]> filteredList;

        try {
            filteredList = recordsList.stream()
                    .filter(strings -> strings[3].equals(localeName))
                    .collect(Collectors.toList());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new QuestionCSVFormatException(e.getMessage());
        }

        for (String[] records : filteredList) {
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
            return createQuestionsList(readCsv(this.csvResourcePath), this.locale);
        } catch (QuestionFormatException|QuestionCSVFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
