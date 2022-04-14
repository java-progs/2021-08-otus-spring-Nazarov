package ru.otus.homework.service;

import org.springframework.stereotype.Service;
import ru.otus.homework.domain.Caterpillar;

@Service
public class BirdServiceImpl implements BirdService {

    @Override
    public void eatCaterpillar(Caterpillar caterpillar) {
        System.out.println(String.format(" > > > Bird eating caterpillar: %s", caterpillar.getId()));
    }
}
