package com.axity.dinosaurpark.model.vehicle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Prueba el ciclo de vida del vehiculo sin depender del motor de simulacion.
 */
class VehicleTest {

    @Test
    void startsAvailableAndStoresIdentity() {
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);

        assertEquals(1, vehicle.getId());
        assertEquals("Jeep 1", vehicle.getName());
        assertEquals(5, vehicle.getRepairSteps());
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
        assertEquals(0, vehicle.getRepairCountdown());
    }

    @Test
    void useAndFreeMoveBetweenAvailableAndInUse() {
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);

        assertTrue(vehicle.use());
        assertEquals(VehicleStatus.IN_USE, vehicle.getStatus());

        assertTrue(vehicle.free());
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void cannotUseUnavailableVehicleOrFreeVehicleThatIsNotInUse() {
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);

        assertFalse(vehicle.free());
        vehicle.markBroken();

        assertFalse(vehicle.use());
        assertEquals(VehicleStatus.BROKEN, vehicle.getStatus());
    }

    @Test
    void brokenVehicleRepairsItselfAfterConfiguredTicks() {
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 2);

        vehicle.markBroken();
        assertEquals(VehicleStatus.BROKEN, vehicle.getStatus());
        assertEquals(2, vehicle.getRepairCountdown());

        vehicle.tick();
        assertEquals(VehicleStatus.BROKEN, vehicle.getStatus());
        assertEquals(1, vehicle.getRepairCountdown());

        vehicle.tick();
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
        assertEquals(0, vehicle.getRepairCountdown());
    }

    @Test
    void repairStepsCannotBeLowerThanOne() {
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 0);

        vehicle.markBroken();
        vehicle.tick();

        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }
}
