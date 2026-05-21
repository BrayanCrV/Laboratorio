package com.axity.dinosaurpark.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.event.TestParkLedger;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/**
 * Prueba el estado compartido que los eventos usaran durante la simulacion.
 */
class ParkStateTest {

    @Test
    void tracksActiveEventsForCurrentStep() {
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            new TestParkLedger()
        );

        state.registerActiveEvent("APAGON_MASIVO");
        assertEquals(1, state.getActiveEventNames().size());

        state.clearActiveEvents();
        assertTrue(state.getActiveEventNames().isEmpty());
    }
}
