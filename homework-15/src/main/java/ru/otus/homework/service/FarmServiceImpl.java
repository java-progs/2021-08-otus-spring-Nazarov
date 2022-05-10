package ru.otus.homework.service;

import org.springframework.stereotype.Service;
import ru.otus.homework.domain.*;

@Service
public class FarmServiceImpl implements FarmService {

    @Override
    public Caterpillar raiseCaterpillar(Egg egg) {
        System.out.println(String.format("Raise caterpillar %s", egg.getId()));
        return new Caterpillar(egg.getId(), egg.getSpecies());
    }

    @Override
    public Chrysalis raiseChrysalis(Caterpillar caterpillar) {
        System.out.println(String.format("Raise chrysalis %s", caterpillar.getId()));
        return new Chrysalis(caterpillar.getId(), caterpillar.getSpecies());
    }

    @Override
    public Butterfly raiseButterfly(Chrysalis chrysalis) {
        System.out.println(String.format("Raise butterfly %s %s", chrysalis.getId(), chrysalis.getSpecies()));
        return new Butterfly(chrysalis.getId(), chrysalis.getSpecies());
    }

}
