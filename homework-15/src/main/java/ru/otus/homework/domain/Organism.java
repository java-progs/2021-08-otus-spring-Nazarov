package ru.otus.homework.domain;

import java.util.UUID;

public abstract class Organism {

    private final UUID id;
    private final String species;

    public Organism(UUID id, String species) {
        this.id = id;
        this.species = species;
    }

    public UUID getId() {
        return id;
    }

    public String getSpecies() {
        return species;
    }

}
