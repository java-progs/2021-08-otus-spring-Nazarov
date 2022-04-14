package ru.otus.homework.service;

import ru.otus.homework.domain.*;

public interface FarmService {
    
    Caterpillar raiseCaterpillar(Egg egg);
    
    Chrysalis raiseChrysalis(Caterpillar caterpillar);
    
    Butterfly raiseButterfly(Chrysalis chrysalis);

}
