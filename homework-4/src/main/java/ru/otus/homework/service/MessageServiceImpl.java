package ru.otus.homework.service;

import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private final LocalizationService localizationService;
    private final IOService ioService;

    public MessageServiceImpl(LocalizationService localizationService, IOService ioService) {
        this.localizationService = localizationService;
        this.ioService = ioService;
    }

    @Override
    public void showMessage(String message, Object... args) {
        ioService.sendMessage(getMessage(message, args));
    }

    @Override
    public String getMessage(String message, Object... args) {
        return localizationService.getLocalizationMessage(message, args);
    }

    @Override
    public String readMessage() {
        return ioService.getMessage();
    }

}
