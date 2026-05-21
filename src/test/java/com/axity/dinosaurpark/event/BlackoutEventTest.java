package com.axity.dinosaurpark.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Confirma que un apagon deja la planta apagada y registra sus costos.
 */
class BlackoutEventTest {

    @Test
    void executeTurnsOffPowerPlantAndRegistersExpenseAndEvent() {
        TestParkLedger ledger = new TestParkLedger();
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);
        ParkState state = new ParkState(new ArrayList<>(), new ArrayList<>(), plant, ledger);
        state.setCurrentStep(3);

        BlackoutEvent event = new BlackoutEvent(0.03);
        event.execute(state, new Random(1));

        assertEquals("APAGON_MASIVO", event.getName());
        assertEquals(0.03, event.getProbability());
        assertFalse(plant.isOperational());
        assertEquals(0.0, plant.getCurrentEnergy());
        assertEquals(1, ledger.getExpenses().size());
        assertEquals(2000.0, ledger.getExpenses().get(0).amount());
        assertEquals(1, ledger.getEvents().size());
        assertEquals(3, ledger.getEvents().get(0).step());
    }
}
