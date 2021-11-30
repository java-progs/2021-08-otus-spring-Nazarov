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
import ru.otus.homework.provider.LocaleProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionDaoCsv implements QuestionDao {

    private final CSVConfig config;
    private final LocaleProvider localeProvider;

    public QuestionDaoCsv(CSVConfig config, LocaleProvider localeProvider) {
        this.config = config;
        this.localeProvider = localeProvider;
    }

    private List<String[]> readCsv(String resourcePath) {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(config.getSeparator())
                .withIgnoreQuotations(config.isIgnoreQuotations())
                .build();

        Resource csvResource = new ClassPathResource(resourcePath);

        try (Reader fileReader = new InputStreamReader(csvResource.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(fileReader)
                     .withSkipLines(config.getSkipLines())
                     .withCSVParser(parser)
                     .build()) {
            return csvReader.readAll();
        } catch (IOException|CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Question> createQuestionsList(List<String[]> recordsList) throws QuestionFormatException,
            QuestionCSVFormatException {
        List<Question> questionList = new ArrayList<>();
        String localeName = localeProvider.getLocale().toString();
        List<String[]> filteredList;

        try {
            filteredList = recordsList.stream()
                    .filter(strings -> strings[3].equalsIgnoreCase(localeName))
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
            return createQuestionsList(readCsv(config.getResourcePath()));
        } catch (QuestionFormatException|QuestionCSVFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
