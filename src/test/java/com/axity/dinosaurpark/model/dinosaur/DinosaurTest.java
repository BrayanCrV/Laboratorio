package com.axity.dinosaurpark.model.dinosaur;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DinosaurTest {

    @Test
    void carnivoreHasConfiguredDietDangerAndFeedingCost() {
        Dinosaur dinosaur = new CarnivoreDinosaur(1, "Rex", "Tyrannosaurus");

        assertEquals(1, dinosaur.getId());
        assertEquals("Rex", dinosaur.getName());
        assertEquals("Tyrannosaurus", dinosaur.getSpecies());
        assertEquals(DinosaurStatus.IN_ENCLOSURE, dinosaur.getStatus());
        assertEquals("CARNIVORE", dinosaur.getDiet());
        assertEquals(0.9, dinosaur.getDangerLevel());
        assertEquals(500.0, dinosaur.getFeedingCostPerDay());
    }

    @Test
    void herbivoreHasConfiguredDietDangerAndFeedingCost() {
        Dinosaur dinosaur = new HerbivoreDinosaur(2, "Luna", "Triceratops");

        assertEquals("HERBIVORE", dinosaur.getDiet());
        assertEquals(0.2, dinosaur.getDangerLevel());
        assertEquals(200.0, dinosaur.getFeedingCostPerDay());
    }

    @Test
    void dinosaurStatusChangesThroughDomainMethods() {
        Dinosaur dinosaur = new CarnivoreDinosaur(1, "Rex", "Tyrannosaurus");

        dinosaur.escape();
        assertEquals(DinosaurStatus.ESCAPED, dinosaur.getStatus());

        dinosaur.recapture();
        assertEquals(DinosaurStatus.RECAPTURED, dinosaur.getStatus());

        dinosaur.returnToEnclosure();
        assertEquals(DinosaurStatus.IN_ENCLOSURE, dinosaur.getStatus());
    }
}
