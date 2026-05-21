package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.vehicle.VehicleStatus;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Fotografia viva del parque; los eventos la usan para modificar lo que esta pasando.
 */
@Getter
public class ParkState {

    private final List<Tourist> tourists;
    private final List<Dinosaur> dinosaurs;
    private final List<Vehicle> vehicles;
    private final PowerPlant powerPlant;
    private final ParkLedger ledger;
    private final List<String> activeEventNames = new ArrayList<>();
    private boolean dealsHourActive;
    private double currentDiscount;
    @Setter
    private long currentStep;

    public ParkState(
        List<Tourist> tourists,
        List<Dinosaur> dinosaurs,
        PowerPlant powerPlant,
        ParkLedger ledger
    ) {
        this(tourists, dinosaurs, Collections.emptyList(), powerPlant, ledger);
    }

    public ParkState(
        List<Tourist> tourists,
        List<Dinosaur> dinosaurs,
        List<Vehicle> vehicles,
        PowerPlant powerPlant,
        ParkLedger ledger
    ) {
        this.tourists = tourists;
        this.dinosaurs = dinosaurs;
        this.vehicles = vehicles;
        this.powerPlant = powerPlant;
        this.ledger = ledger;
    }

    public void registerActiveEvent(String eventName) {
        activeEventNames.add(eventName);
    }

    public void clearActiveEvents() {
        activeEventNames.clear();
        dealsHourActive = false;
        currentDiscount = 0.0;
    }

    public void activateDealsHour(double discount) {
        dealsHourActive = true;
        currentDiscount = Math.max(0.0, Math.min(1.0, discount));
    }

    public int countVehiclesInUse() {
        return (int) vehicles.stream()
            .filter(vehicle -> vehicle.getStatus() == VehicleStatus.IN_USE
                || vehicle.getStatus() == VehicleStatus.BROKEN)
            .count();
    }
}
