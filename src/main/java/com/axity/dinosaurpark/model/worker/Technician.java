package com.axity.dinosaurpark.model.worker;

import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.vehicle.VehicleStatus;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.List;
import java.util.Optional;

/**
 * Tecnico del parque; repara la planta solo si puede tomar un vehiculo disponible.
 */
public class Technician extends Worker {

    public Technician(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "TECHNICIAN";
    }

    public boolean repairIfNeeded(PowerPlant plant, List<Vehicle> vehicles) {
        if (plant == null || vehicles == null || plant.isOperational()) {
            return false;
        }

        Optional<Vehicle> availableVehicle = vehicles.stream()
            .filter(vehicle -> vehicle.getStatus() == VehicleStatus.AVAILABLE)
            .findFirst();

        if (availableVehicle.isEmpty()) {
            return false;
        }

        Vehicle vehicle = availableVehicle.get();
        vehicle.use();
        plant.repair();
        vehicle.free();
        return true;
    }
}
