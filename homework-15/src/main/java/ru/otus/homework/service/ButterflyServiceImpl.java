package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Butterfly;
import ru.otus.homework.domain.Egg;
import ru.otus.homework.integration.ButterflyFarm;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ButterflyServiceImpl implements ButterflyService {

    private final ButterflyFarm farm;
    private final List<String> speciesList = List.of("Monarch", "Moth", "Cabbage white");

    @Override
    public void startButterflyLoop() {
        val random = new Random();
        for (int i=0; i < 100; i++) {
            val species = speciesList.get(random.nextInt(speciesList.size()));
            Egg egg = new Egg(UUID.randomUUID(), species);
            System.out.println("New egg " + egg.getId() + " " + egg.getSpecies());
            farm.raise(egg);

            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void butterflyFlight(Butterfly butterfly, String destination) {
        System.out.println(String.format(" >I< >I< >I< Butterfly %s, id=%s flies on %s",
                butterfly.getSpecies(), butterfly.getId(), destination));
    }
}
