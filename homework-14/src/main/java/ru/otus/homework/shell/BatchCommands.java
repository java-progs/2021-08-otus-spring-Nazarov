package ru.otus.homework.shell;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDateTime;

import static ru.otus.homework.config.JobMongoConfig.JOB_MIGRATE;

@ShellComponent
@RequiredArgsConstructor
public class BatchCommands {

    private final JobOperator jobOperator;
    private long executionId;

    @ShellMethod(value = "startMigration", key = "sm")
    public void startMigration() throws Exception {
        try {
            executionId = runJob("");
            System.out.println(jobOperator.getSummary(executionId));
        } catch (JobInstanceAlreadyExistsException e) {
            System.out.println("Job is completed. User rm to restart job");
        }
    }

    @ShellMethod(value = "restartMigration", key = "rm")
    public void restartMigration() throws Exception {
        val time = LocalDateTime.now().toString();
        executionId = runJob(time);
        System.out.println(jobOperator.getSummary(executionId));
    }

    private long runJob(String parameters) throws Exception {
        return jobOperator.start(JOB_MIGRATE, parameters);
    }
}
