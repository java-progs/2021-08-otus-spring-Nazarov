package ru.otus.homework.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.TimeoutCountSequenceSizeReleaseStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;
import ru.otus.homework.domain.Butterfly;
import ru.otus.homework.domain.Caterpillar;
import ru.otus.homework.service.BirdService;
import ru.otus.homework.service.ButterflyService;
import ru.otus.homework.service.FarmService;

import java.util.List;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class IntegrationConfig {

    private final String RAISE_CATERPILLAR_METHOD = "raiseCaterpillar";
    private final String RAISE_CHRYSALIS_METHOD = "raiseChrysalis";
    private final String RAISE_BUTTERFLY_METHOD = "raiseButterfly";

    private final BirdService birdService;
    private final ButterflyService butterflyService;

    @Bean
    public SubscribableChannel birdBreakfastChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @ServiceActivator(inputChannel = "birdBreakfastChannel")
    public void birdEats(Caterpillar caterpillar) {
        birdService.eatCaterpillar(caterpillar);
    }

    @ServiceActivator(inputChannel = "cabbageChannel")
    public void butterflyFlightOnCabbage(List<Butterfly> butterflyList) {
        for (Butterfly b : butterflyList) {
            butterflyService.butterflyFlight(b, "cabbages");
        }
    }

    @ServiceActivator(inputChannel = "flowerChannel")
    public void butterflyFlightOnFlower(List<Butterfly> butterflyList) {
        for (Butterfly b : butterflyList) {
            butterflyService.butterflyFlight(b, "flowers");
        }
    }

    @ServiceActivator(inputChannel = "lightChannel")
    public void butterflyFlightOnLight(List<Butterfly> butterflyList) {
        for (Butterfly b : butterflyList) {
            butterflyService.butterflyFlight(b, "light");
        }
    }

    @Bean
    public IntegrationFlow caterpillarFlow(FarmService service) {
        return flow -> flow.handle(service, RAISE_CATERPILLAR_METHOD)
                .<Caterpillar, Boolean>route(c -> new Random().nextBoolean(),
                        m -> m.subFlowMapping(true, butterflyFlow(service))
                                .subFlowMapping(false, birdBreakfastFlow())
                        );
    }

    @Bean
    public IntegrationFlow birdBreakfastFlow() {
        return f -> f.channel(birdBreakfastChannel());
    }

    @Bean
    public IntegrationFlow butterflyFlow(FarmService service) {
        return f -> f.handle(service, RAISE_CHRYSALIS_METHOD)
                .handle(service, RAISE_BUTTERFLY_METHOD)
                .aggregate(aggregator -> aggregator
                        .correlationStrategy(m -> ((Butterfly) m.getPayload()).getSpecies())
                        .expireGroupsUponCompletion(true)
                        .expireGroupsUponTimeout(true)
                        .releaseStrategy(new TimeoutCountSequenceSizeReleaseStrategy(5, 5000L))
                )
                .<List<Butterfly>, String>route(l -> l.get(0).getSpecies(),
                        s -> s.channelMapping("Monarch", "flowerChannel")
                                .channelMapping("Cabbage white", "cabbageChannel")
                                .channelMapping("Moth", "lightChannel")
                        );
    }

}
