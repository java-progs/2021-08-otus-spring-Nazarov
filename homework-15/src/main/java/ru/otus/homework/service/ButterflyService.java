package ru.otus.homework.service;

import ru.otus.homework.domain.Butterfly;

public interface ButterflyService {

    void startButterflyLoop();

    void butterflyFlight(Butterfly butterfly, String destination);
}
