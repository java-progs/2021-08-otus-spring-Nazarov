package ru.otus.homework.service;

import java.util.Locale;

public interface MessageService {

    void showMessage(String message, Object... args);

    String readMessage();

    String getMessage(String message, Object... args);

}
