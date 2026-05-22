package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.dinosaur.DinosaurStatus;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.vehicle.VehicleStatus;
import com.axity.dinosaurpark.model.worker.Worker;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.persistence.RevenueRecord;
import com.axity.dinosaurpark.zone.ArrivalZone;
import com.axity.dinosaurpark.zone.BathroomZone;
import com.axity.dinosaurpark.zone.CentralHub;
import com.axity.dinosaurpark.zone.ObservationEnclosure;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

/**
 * Fotografia viva del parque; los eventos la usan para modificar lo que esta pasando.
 */
@Getter
public class ParkState implements ParkLedger {

    private final List<Tourist> tourists;
    private final List<Dinosaur> dinosaurs;
    private final List<Vehicle> vehicles;
    private final PowerPlant powerPlant;
    private final ParkLedger ledger;
    private final List<Worker> workers;
    private final ArrivalZone arrivalZone;
    private final CentralHub centralHub;
    private final BathroomZone bathroomZone;
    private final ObservationEnclosure basicEnclosure;
    private final ObservationEnclosure premiumEnclosure;
    private final ObservationEnclosure vipEnclosure;
    private final Random rng;
    private final List<String> activeEventNames = new ArrayList<>();
    private double totalRevenue;
    private double totalExpenses;
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
        this(tourists, dinosaurs, Collections.emptyList(), powerPlant, ledger,
             Collections.emptyList(), null, null, null, null, null, null, new Random());
    }

    public ParkState(
        List<Tourist> tourists,
        List<Dinosaur> dinosaurs,
        List<Vehicle> vehicles,
        PowerPlant powerPlant,
        ParkLedger ledger
    ) {
        this(tourists, dinosaurs, vehicles, powerPlant, ledger,
             Collections.emptyList(), null, null, null, null, null, null, new Random());
    }

    public ParkState(
        List<Tourist> tourists,
        List<Dinosaur> dinosaurs,
        List<Vehicle> vehicles,
        PowerPlant powerPlant,
        ParkLedger ledger,
        List<Worker> workers,
        ArrivalZone arrivalZone,
        CentralHub centralHub,
        BathroomZone bathroomZone,
        ObservationEnclosure basicEnclosure,
        ObservationEnclosure premiumEnclosure,
        ObservationEnclosure vipEnclosure,
        Random rng
    ) {
        this.tourists = tourists;
        this.dinosaurs = dinosaurs;
        this.vehicles = vehicles;
        this.powerPlant = powerPlant;
        this.ledger = ledger;
        this.workers = workers;
        this.arrivalZone = arrivalZone;
        this.centralHub = centralHub;
        this.bathroomZone = bathroomZone;
        this.basicEnclosure = basicEnclosure;
        this.premiumEnclosure = premiumEnclosure;
        this.vipEnclosure = vipEnclosure;
        this.rng = rng;
    }

    public void incrementStep() {
        currentStep++;
    }

    public void addRevenue(double amount) {
        if (amount > 0) {
            totalRevenue += amount;
        }
    }

    public void addExpense(double amount) {
        if (amount > 0) {
            totalExpenses += amount;
        }
    }

    public int countActiveTourists() {
        return (int) tourists.stream()
            .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
            .count();
    }

    public int countDinosaursInEnclosure() {
        return (int) dinosaurs.stream()
            .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
            .count();
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

    @Override
    public void appendRevenue(RevenueRecord record) {
        addRevenue(record.amount());
        ledger.appendRevenue(record);
    }

    @Override
    public void appendExpense(ExpenseRecord record) {
        addExpense(record.amount());
        ledger.appendExpense(record);
    }

    @Override
    public void appendEvent(EventRecord record) {
        ledger.appendEvent(record);
    }
}
