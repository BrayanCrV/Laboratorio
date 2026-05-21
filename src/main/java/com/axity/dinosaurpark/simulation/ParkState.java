package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.persistence.ParkLedger;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
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
    private final PowerPlant powerPlant;
    private final ParkLedger ledger;
    private final List<String> activeEventNames = new ArrayList<>();
    @Setter
    private long currentStep;

    public ParkState(
        List<Tourist> tourists,
        List<Dinosaur> dinosaurs,
        PowerPlant powerPlant,
        ParkLedger ledger
    ) {
        this.tourists = tourists;
        this.dinosaurs = dinosaurs;
        this.powerPlant = powerPlant;
        this.ledger = ledger;
    }

    public void registerActiveEvent(String eventName) {
        activeEventNames.add(eventName);
    }

    public void clearActiveEvents() {
        activeEventNames.clear();
    }
}
