package ru.otus.homework.service;

import org.springframework.lang.Nullable;

public interface LocalizationService {

    String getLocalizationMessage(String message, @Nullable Object... args);

}
