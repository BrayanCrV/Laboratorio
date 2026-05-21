package com.axity.dinosaurpark.model.dinosaur;

/**
 * Dinosaurio herbivoro: sigue siendo enorme, pero representa menos riesgo para los turistas.
 */
public class HerbivoreDinosaur extends Dinosaur {

    private static final double FEEDING_COST_PER_DAY = 200.0;
    private static final double DANGER_LEVEL = 0.2;

    public HerbivoreDinosaur(int id, String name, String species) {
        super(id, name, species, FEEDING_COST_PER_DAY);
    }

    @Override
    public String getDiet() {
        return "HERBIVORE";
    }

    @Override
    public double getDangerLevel() {
        return DANGER_LEVEL;
    }
}
