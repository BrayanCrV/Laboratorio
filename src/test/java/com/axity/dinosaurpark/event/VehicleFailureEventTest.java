package com.axity.dinosaurpark.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.vehicle.VehicleStatus;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Prueba que una falla de vehiculo rompa solo unidades disponibles.
 */
class VehicleFailureEventTest {

    @Test
    void executeBreaksOneAvailableVehicleAndRegistersEvent() {
        TestParkLedger ledger = new TestParkLedger();
        Vehicle first = new Vehicle(1, "Jeep 1", 5);
        Vehicle second = new Vehicle(2, "Jeep 2", 5);
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            List.of(first, second),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            ledger
        );
        state.setCurrentStep(9);

        VehicleFailureEvent event = new VehicleFailureEvent(0.06);
        event.execute(state, new Random(1));

        long brokenVehicles = state.getVehicles().stream()
            .filter(vehicle -> vehicle.getStatus() == VehicleStatus.BROKEN)
            .count();

        assertEquals("FALLA_VEHICULO", event.getName());
        assertEquals(0.06, event.getProbability());
        assertEquals(1, brokenVehicles);
        assertTrue(state.getActiveEventNames().contains("FALLA_VEHICULO"));
        assertEquals(1, ledger.getEvents().size());
        assertEquals(9, ledger.getEvents().get(0).step());
    }

    @Test
    void executeDoesNothingWhenNoVehicleIsAvailable() {
        TestParkLedger ledger = new TestParkLedger();
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);
        vehicle.markBroken();
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            List.of(vehicle),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            ledger
        );

        new VehicleFailureEvent(0.06).execute(state, new Random(1));

        assertEquals(VehicleStatus.BROKEN, vehicle.getStatus());
        assertTrue(state.getActiveEventNames().isEmpty());
        assertTrue(ledger.getEvents().isEmpty());
    }
}
