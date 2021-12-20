package ru.otus.homework.service;

public interface MessageService {

    void showMessage(String message, Object... args);

    String readMessage();

    String getMessage(String message, Object... args);

}
