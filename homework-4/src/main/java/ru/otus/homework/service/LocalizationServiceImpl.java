package ru.otus.homework.service;

import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.otus.homework.provider.LocaleProvider;

@Service
public class LocalizationServiceImpl implements LocalizationService {

    private final MessageSource messageSource;
    private final LocaleProvider localeProvider;

    public LocalizationServiceImpl(MessageSource messageSource, LocaleProvider localeProvider) {
        this.messageSource = messageSource;
        this.localeProvider = localeProvider;
    }

    @Override
    public String getLocalizationMessage(String messageName, @Nullable Object... args) {
        return messageSource.getMessage(messageName, args, localeProvider.getLocale());
    }

}
