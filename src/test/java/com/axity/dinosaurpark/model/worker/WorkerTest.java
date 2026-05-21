package com.axity.dinosaurpark.model.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.dinosaur.CarnivoreDinosaur;
import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.dinosaur.DinosaurStatus;
import com.axity.dinosaurpark.model.dinosaur.HerbivoreDinosaur;
import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.vehicle.VehicleStatus;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Pruebas pequenas para confirmar que los trabajadores nacen con su rol correcto.
 */
class WorkerTest {

    @Test
    void guardStoresIdentitySalaryAndRole() {
        Guard guard = new Guard(1, "Muldoon", 150.0);

        assertEquals(1, guard.getId());
        assertEquals("Muldoon", guard.getName());
        assertEquals(150.0, guard.getDailySalary());
        assertEquals("GUARD", guard.getRole());
    }

    @Test
    void technicianStoresIdentitySalaryAndRole() {
        Technician technician = new Technician(2, "Ellie", 180.0);

        assertEquals(2, technician.getId());
        assertEquals("Ellie", technician.getName());
        assertEquals(180.0, technician.getDailySalary());
        assertEquals("TECHNICIAN", technician.getRole());
    }

    @Test
    void guardReturnsEscapedDinosaursToTheirEnclosure() {
        Guard guard = new Guard(1, "Muldoon", 150.0);
        Dinosaur carnivore = new CarnivoreDinosaur(1, "Rex", "Tyrannosaurus");
        Dinosaur herbivore = new HerbivoreDinosaur(2, "Luna", "Triceratops");
        carnivore.escape();

        guard.recaptureEscapedDinosaurs(List.of(carnivore, herbivore));

        assertEquals(DinosaurStatus.IN_ENCLOSURE, carnivore.getStatus());
        assertEquals(DinosaurStatus.IN_ENCLOSURE, herbivore.getStatus());
    }

    @Test
    void guardIgnoresMissingDinosaurList() {
        Guard guard = new Guard(1, "Muldoon", 150.0);

        guard.recaptureEscapedDinosaurs(null);

        assertEquals("GUARD", guard.getRole());
    }

    @Test
    void technicianRepairsPlantWhenVehicleIsAvailable() {
        Technician technician = new Technician(2, "Ellie", 180.0);
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);
        plant.triggerFailure();

        boolean repaired = technician.repairIfNeeded(plant, List.of(vehicle));

        assertTrue(repaired);
        assertTrue(plant.isOperational());
        assertEquals(100.0, plant.getCurrentEnergy());
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void technicianCannotRepairWithoutAvailableVehicle() {
        Technician technician = new Technician(2, "Ellie", 180.0);
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);
        vehicle.markBroken();
        plant.triggerFailure();

        boolean repaired = technician.repairIfNeeded(plant, List.of(vehicle));

        assertFalse(repaired);
        assertFalse(plant.isOperational());
    }

    @Test
    void technicianIgnoresPlantThatIsAlreadyWorking() {
        Technician technician = new Technician(2, "Ellie", 180.0);
        PowerPlant plant = new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0);
        Vehicle vehicle = new Vehicle(1, "Jeep 1", 5);

        boolean repaired = technician.repairIfNeeded(plant, List.of(vehicle));

        assertFalse(repaired);
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }
}
