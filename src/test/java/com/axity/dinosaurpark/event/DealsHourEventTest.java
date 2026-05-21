package com.axity.dinosaurpark.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Revisa que la hora de ofertas dure solo el step actual.
 */
class DealsHourEventTest {

    @Test
    void executeActivatesDiscountAndRegistersEvent() {
        TestParkLedger ledger = new TestParkLedger();
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            ledger
        );
        state.setCurrentStep(4);

        DealsHourEvent event = new DealsHourEvent(0.08);
        event.execute(state, new Random(1));

        assertEquals("HORA_DE_OFERTAS", event.getName());
        assertEquals(0.08, event.getProbability());
        assertTrue(state.isDealsHourActive());
        assertEquals(0.30, state.getCurrentDiscount());
        assertTrue(state.getActiveEventNames().contains("HORA_DE_OFERTAS"));
        assertEquals(1, ledger.getEvents().size());
        assertEquals(4, ledger.getEvents().get(0).step());
    }

    @Test
    void clearActiveEventsRemovesDiscount() {
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            new TestParkLedger()
        );

        new DealsHourEvent(0.08).execute(state, new Random(1));
        state.clearActiveEvents();

        assertFalse(state.isDealsHourActive());
        assertEquals(0.0, state.getCurrentDiscount());
        assertTrue(state.getActiveEventNames().isEmpty());
    }
}
