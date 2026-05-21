package com.axity.dinosaurpark.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.dinosaur.CarnivoreDinosaur;
import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.dinosaur.DinosaurStatus;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Revisa la estrategia de escape sin depender todavia del motor completo.
 */
class DinosaurEscapeEventTest {

    @Test
    void executeEscapesDinosaurAndCanAttackTourist() {
        TestParkLedger ledger = new TestParkLedger();
        Dinosaur dinosaur = new CarnivoreDinosaur(1, "Rex", "Tyrannosaurus");
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        ParkState state = new ParkState(
            List.of(tourist),
            List.of(dinosaur),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            ledger
        );
        state.setCurrentStep(7);

        DinosaurEscapeEvent event = new DinosaurEscapeEvent(0.05);
        event.execute(state, new Random(1));

        assertEquals("ESCAPE_DINOSAURIO", event.getName());
        assertEquals(0.05, event.getProbability());
        assertEquals(DinosaurStatus.ESCAPED, dinosaur.getStatus());
        assertEquals(TouristStatus.ATTACKED, tourist.getStatus());
        assertEquals(1, ledger.getEvents().size());
        assertEquals(7, ledger.getEvents().get(0).step());
        assertTrue(state.getActiveEventNames().contains("ESCAPE_DINOSAURIO"));
    }

    @Test
    void executeWithNoDinosaursDoesNotFailAndStillLogsEvent() {
        TestParkLedger ledger = new TestParkLedger();
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            ledger
        );

        new DinosaurEscapeEvent(0.05).execute(state, new Random(1));

        assertEquals(1, ledger.getEvents().size());
        assertEquals("ESCAPE_DINOSAURIO", ledger.getEvents().get(0).eventName());
    }
}
