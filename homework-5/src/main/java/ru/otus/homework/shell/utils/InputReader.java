package ru.otus.homework.shell.utils;

import org.jline.reader.LineReader;

public class InputReader {

    private final LineReader lineReader;

    public InputReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    public String prompt(String prompt) {
        return lineReader.readLine(String.format("%s : ", prompt));
    }
}
