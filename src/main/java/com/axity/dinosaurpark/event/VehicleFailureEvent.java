package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.vehicle.Vehicle;
import com.axity.dinosaurpark.model.vehicle.VehicleStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Evento donde un vehiculo disponible se descompone y queda fuera de servicio.
 */
public class VehicleFailureEvent implements SimulationEvent {

    private static final String NAME = "FALLA_VEHICULO";
    private final double probability;
    private String lastDescription = "Un vehiculo de mantenimiento fallo.";
    private String lastAffectedEntities = "Vehiculo no identificado";

    public VehicleFailureEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Un vehiculo de mantenimiento queda temporalmente fuera de uso.";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        List<Vehicle> availableVehicles = state.getVehicles().stream()
            .filter(vehicle -> vehicle.getStatus() == VehicleStatus.AVAILABLE)
            .toList();

        if (availableVehicles.isEmpty()) {
            return;
        }

        Vehicle selected = availableVehicles.get(rng.nextInt(availableVehicles.size()));
        selected.markBroken();
        state.registerActiveEvent(NAME);
        lastDescription = selected.getName() + " quedo fuera de servicio.";
        lastAffectedEntities = "Vehiculo: " + selected.getName();
        state.getLedger().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(
            step,
            NAME,
            lastDescription,
            lastAffectedEntities,
            LocalDateTime.now()
        );
    }
}
