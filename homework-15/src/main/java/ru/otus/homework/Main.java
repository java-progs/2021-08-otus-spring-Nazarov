package ru.otus.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.SubscribableChannel;
import ru.otus.homework.domain.Butterfly;
import ru.otus.homework.domain.Caterpillar;
import ru.otus.homework.service.ButterflyService;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);

        ButterflyService service = context.getBean(ButterflyService.class);
        service.startButterflyLoop();
    }
}
