package ru.otus.homework.provider;

import org.springframework.stereotype.Component;
import ru.otus.homework.config.LocaleConfig;

import java.util.Locale;

@Component
public class LocaleProvider {
    private Locale locale;

    public LocaleProvider(LocaleConfig config) {
        this.locale = config.getLocale();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
