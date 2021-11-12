package ru.otus.homework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "csv")
@Component
public class CSVConfig {

    private String path;

    private char separator;

    private boolean ignoreQuotations;

    private int skipLines;

    public String getResourcePath() {
        return path;
    }

    public void setResourcePath(String path) {
        this.path = path;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char csvSeparator) {
        this.separator = csvSeparator;
    }

    public boolean isIgnoreQuotations() {
        return ignoreQuotations;
    }

    public void setIgnoreQuotations(boolean ignoreQuotations) {
        this.ignoreQuotations = ignoreQuotations;
    }

    public int getSkipLines() {
        return skipLines;
    }

    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }

}
