package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.simulation.ParkState;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Evento de apagon: la planta queda fuera de servicio y aparece un gasto fuerte.
 */
public class BlackoutEvent implements SimulationEvent {

    private static final String NAME = "APAGON_MASIVO";
    private static final double BLACKOUT_COST = 2000.0;
    private final double probability;
    private String lastDescription = "La energia del parque fallo por completo.";

    public BlackoutEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "La planta electrica se apaga y el parque entra en emergencia.";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        state.registerActiveEvent(NAME);
        state.getPowerPlant().triggerFailure();
        state.getLedger().appendExpense(new ExpenseRecord(
            0L,
            "BLACKOUT",
            BLACKOUT_COST,
            "Costo operativo por apagon masivo",
            LocalDateTime.now()
        ));
        lastDescription = "Apagon masivo; la planta quedo fuera de servicio.";
        state.getLedger().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(
            step,
            NAME,
            lastDescription,
            "Planta de energia",
            LocalDateTime.now()
        );
    }
}
