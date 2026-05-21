package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.simulation.ParkState;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Tormenta torrencial: obliga a mover a los turistas activos a una evacuacion.
 */
public class StormEvent implements SimulationEvent {

    private static final String NAME = "TORMENTA_TORRENCIAL";
    private static final double STORM_COST = 500.0;
    private final double probability;
    private String lastDescription = "Una tormenta obligo a evacuar zonas del parque.";
    private int affectedTourists;

    public StormEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Lluvia intensa que interrumpe la experiencia normal de los visitantes.";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        state.registerActiveEvent(NAME);
        affectedTourists = 0;
        state.getTourists().stream()
            .filter(tourist -> tourist.getStatus() == TouristStatus.IN_PARK)
            .forEach(tourist -> {
                tourist.recordVisit("Evacuacion");
                affectedTourists++;
            });

        state.getLedger().appendExpense(new ExpenseRecord(
            0L,
            "STORM",
            STORM_COST,
            "Costo operativo por tormenta torrencial",
            LocalDateTime.now()
        ));
        lastDescription = "Tormenta torrencial; turistas activos enviados a evacuacion.";
        state.getLedger().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(
            step,
            NAME,
            lastDescription,
            "Turistas afectados: " + affectedTourists,
            LocalDateTime.now()
        );
    }
}
