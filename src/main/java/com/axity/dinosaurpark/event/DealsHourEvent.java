package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Evento de ofertas: durante un step baja el precio de boletos y souvenirs.
 */
public class DealsHourEvent implements SimulationEvent {

    private static final String NAME = "HORA_DE_OFERTAS";
    private static final double DISCOUNT = 0.30;
    private final double probability;

    public DealsHourEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "El parque activa descuentos temporales para atraer mas compras.";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        state.registerActiveEvent(NAME);
        state.activateDealsHour(DISCOUNT);
        state.getLedger().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(
            step,
            NAME,
            "Hora de ofertas activada con 30% de descuento.",
            "Boletos y souvenirs",
            LocalDateTime.now()
        );
    }
}
