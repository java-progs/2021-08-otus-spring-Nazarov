package ru.otus.homework.service;

import org.springframework.lang.Nullable;

import java.util.Locale;

public interface LocalizationService {

    String getLocalizationMessage(String message, @Nullable Object[] args);

    void setLocale(Locale locale);

}
