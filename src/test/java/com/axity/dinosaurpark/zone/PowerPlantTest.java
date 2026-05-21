package com.axity.dinosaurpark.zone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Prueba la energia de la planta sin meter todavia eventos ni tecnicos.
 */
class PowerPlantTest {

    @Test
    void tickConsumesEnergyWithoutGoingBelowZero() {
        PowerPlant plant = new PowerPlant(10.0, 4.0, 0.0, 200.0, 500.0);

        plant.tick(new Random(1), new InMemoryParkLedger());
        plant.tick(new Random(1), new InMemoryParkLedger());
        plant.tick(new Random(1), new InMemoryParkLedger());

        assertEquals(0.0, plant.getCurrentEnergy());
        assertFalse(plant.isOperational());
    }

    @Test
    void triggerFailureTurnsPlantOff() {
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);

        plant.triggerFailure();

        assertFalse(plant.isOperational());
        assertEquals(0.0, plant.getCurrentEnergy());
    }

    @Test
    void repairRestoresEnergyAndRegistersExpense() {
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();
        plant.triggerFailure();

        plant.repair(ledger);

        assertTrue(plant.isOperational());
        assertEquals(100.0, plant.getCurrentEnergy());
        assertEquals(1, ledger.getExpenses().size());
        assertEquals("POWER_REPAIR", ledger.getExpenses().get(0).type());
    }

    @Test
    void maintenanceRegistersOperationalExpense() {
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();

        plant.performMaintenance(ledger);

        assertEquals(1, ledger.getExpenses().size());
        assertEquals(200.0, ledger.getExpenses().get(0).amount());
    }
}
