package ru.otus.homework.config;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.otus.homework.shell.utils.InputReader;
import ru.otus.homework.shell.utils.ShellHelper;

@Configuration
public class SpringShellConfig {

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal,
                                   @Value("${shell.out.info}") String infoColor,
                                   @Value("${shell.out.success}") String successColor,
                                   @Value("${shell.out.warning}") String warningColor,
                                   @Value("${shell.out.error}") String errorColor) {
        return new ShellHelper(terminal, infoColor, successColor, warningColor, errorColor);
    }

    @Bean
    public InputReader inputReader(@Lazy LineReader lineReader) {
        return new InputReader(lineReader);
    }
}
