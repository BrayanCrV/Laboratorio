package com.axity.dinosaurpark.model.dinosaur;

import lombok.Getter;
import lombok.ToString;

/**
 * Base comun para todos los dinosaurios que viven en el parque.
 */
@Getter
@ToString
public abstract class Dinosaur {

    private final int id;
    private final String name;
    private final String species;
    private DinosaurStatus status = DinosaurStatus.IN_ENCLOSURE;
    private final double feedingCostPerDay;

    protected Dinosaur(int id, String name, String species, double feedingCostPerDay) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.feedingCostPerDay = feedingCostPerDay;
    }

    public abstract String getDiet();

    public abstract double getDangerLevel();

    public void escape() {
        status = DinosaurStatus.ESCAPED;
    }

    public void recapture() {
        status = DinosaurStatus.RECAPTURED;
    }

    public void returnToEnclosure() {
        status = DinosaurStatus.IN_ENCLOSURE;
    }
}
