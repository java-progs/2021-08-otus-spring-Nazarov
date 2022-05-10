package ru.otus.homework.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.homework.domain.Egg;

@MessagingGateway
public interface ButterflyFarm {

    @Gateway(requestChannel = "caterpillarFlow.input")
    void raise(Egg egg);

}
