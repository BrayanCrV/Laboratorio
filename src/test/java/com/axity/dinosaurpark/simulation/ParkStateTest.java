package com.axity.dinosaurpark.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.event.TestParkLedger;
import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void countsVehiclesThatAreNotAvailable() {
        Vehicle available = new Vehicle(1, "Jeep 1", 5);
        Vehicle inUse = new Vehicle(2, "Jeep 2", 5);
        Vehicle broken = new Vehicle(3, "Jeep 3", 5);
        inUse.use();
        broken.markBroken();
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            List.of(available, inUse, broken),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            new TestParkLedger()
        );

        assertEquals(2, state.countVehiclesInUse());
    }

    @Test
    void activateDealsHourKeepsDiscountInsideValidRange() {
        ParkState state = new ParkState(
            new ArrayList<>(),
            new ArrayList<>(),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            new TestParkLedger()
        );

        state.activateDealsHour(2.0);
        assertEquals(1.0, state.getCurrentDiscount());

        state.activateDealsHour(-1.0);
        assertEquals(0.0, state.getCurrentDiscount());
    }
}
