package ru.otus.homework;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.homework.domain.AuthorMongo;
import ru.otus.homework.domain.BookMongo;
import ru.otus.homework.domain.GenreMongo;
import ru.otus.homework.domain.UserMongo;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
public class JobTest {

    public static final String JOB_NAME = "migrateDbToMongoJob";
    public static final int EXPECTED_STEP_COUNT = 7;

    public static final int EXPECTED_GENRE_COUNT = 3;
    public static final int EXPECTED_AUTHOR_COUNT = 2;
    public static final int EXPECTED_BOOK_COUNT = 2;
    public static final int EXPECTED_USER_COUNT = 3;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoOperations operations;

    @BeforeEach
    public void prepare() {
        jobRepositoryTestUtils.removeJobExecutions();
        operations.executeCommand("{dropDatabase: 1}");
    }

    @Test
    public void jobShouldSuccessExecute() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();
        ExitStatus jobExistStatus = jobExecution.getExitStatus();
        Collection stepExecution = jobExecution.getStepExecutions();

        assertThat(jobInstance.getJobName()).isEqualTo(JOB_NAME);
        assertThat(stepExecution.size()).isEqualTo(EXPECTED_STEP_COUNT);
        assertThat(jobExistStatus.getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    public void jobShouldCorrectMigrateDb() throws Exception {
        assertThat(operations.findAll(GenreMongo.class).size()).isEqualTo(0);
        assertThat(operations.findAll(AuthorMongo.class).size()).isEqualTo(0);
        assertThat(operations.findAll(BookMongo.class).size()).isEqualTo(0);
        assertThat(operations.findAll(UserMongo.class).size()).isEqualTo(0);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        val genreList = operations.findAll(GenreMongo.class);
        val authorList = operations.findAll(AuthorMongo.class);
        val bookList = operations.findAll(BookMongo.class);

        assertThat(genreList.size()).isEqualTo(EXPECTED_GENRE_COUNT);
        assertThat(authorList.size()).isEqualTo(EXPECTED_AUTHOR_COUNT);
        assertThat(bookList.size()).isEqualTo(EXPECTED_BOOK_COUNT);
        assertThat(operations.findAll(UserMongo.class).size()).isEqualTo(EXPECTED_USER_COUNT);

        assertThat(bookList.get(1).getAuthorsList()).hasSize(1).containsAnyElementsOf(authorList);
        assertThat(bookList.get(1).getGenresList()).hasSize(2).containsAnyElementsOf(genreList);
    }
}
