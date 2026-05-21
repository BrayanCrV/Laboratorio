package com.axity.dinosaurpark.model.vehicle;

import lombok.Getter;
import lombok.ToString;

/**
 * Vehiculo de mantenimiento que los tecnicos necesitan para llegar a reparar fallas.
 */
@Getter
@ToString
public class Vehicle {

    private final int id;
    private final String name;
    private final int repairSteps;
    private VehicleStatus status = VehicleStatus.AVAILABLE;
    private int repairCountdown;

    public Vehicle(int id, String name, int repairSteps) {
        this.id = id;
        this.name = name;
        this.repairSteps = Math.max(1, repairSteps);
    }

    public boolean use() {
        if (status != VehicleStatus.AVAILABLE) {
            return false;
        }
        status = VehicleStatus.IN_USE;
        return true;
    }

    public boolean free() {
        if (status != VehicleStatus.IN_USE) {
            return false;
        }
        status = VehicleStatus.AVAILABLE;
        return true;
    }

    public void markBroken() {
        status = VehicleStatus.BROKEN;
        repairCountdown = repairSteps;
    }

    public void tick() {
        if (status != VehicleStatus.BROKEN) {
            return;
        }

        repairCountdown--;
        if (repairCountdown <= 0) {
            status = VehicleStatus.AVAILABLE;
            repairCountdown = 0;
        }
    }
}
