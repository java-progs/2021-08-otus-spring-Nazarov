package ru.otus.homework.service;

import java.util.Locale;

public interface MessageService {

    public void showMessage(String message, Object[] args);

    public String readMessage();

    public String getMessage(String message, Object[] args);

    public void setLocale(Locale locale);

}
