package com.axity.dinosaurpark.model.dinosaur;

/**
 * Dinosaurio carnivoro: mas caro de alimentar y mucho mas peligroso si escapa.
 */
public class CarnivoreDinosaur extends Dinosaur {

    private static final double FEEDING_COST_PER_DAY = 500.0;
    private static final double DANGER_LEVEL = 0.9;

    public CarnivoreDinosaur(int id, String name, String species) {
        super(id, name, species, FEEDING_COST_PER_DAY);
    }

    @Override
    public String getDiet() {
        return "CARNIVORE";
    }

    @Override
    public double getDangerLevel() {
        return DANGER_LEVEL;
    }
}
