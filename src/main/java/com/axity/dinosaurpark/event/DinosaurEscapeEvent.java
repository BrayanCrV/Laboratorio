package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.dinosaur.Dinosaur;
import com.axity.dinosaurpark.model.dinosaur.DinosaurStatus;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Evento donde un dinosaurio puede escapar y, si es peligroso, atacar a un turista.
 */
public class DinosaurEscapeEvent implements SimulationEvent {

    private static final String NAME = "ESCAPE_DINOSAURIO";
    private final double probability;
    private String lastDescription = "Un dinosaurio intento escapar.";
    private String lastAffectedEntities = "Sin entidades afectadas";

    public DinosaurEscapeEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Un dinosaurio rompe la seguridad de su recinto.";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        state.registerActiveEvent(NAME);

        List<Dinosaur> candidates = state.getDinosaurs().stream()
            .filter(dinosaur -> dinosaur.getStatus() == DinosaurStatus.IN_ENCLOSURE)
            .toList();

        if (candidates.isEmpty()) {
            lastDescription = "No habia dinosaurios disponibles para escapar.";
            lastAffectedEntities = "Sin entidades afectadas";
            state.getLedger().appendEvent(toRecord(state.getCurrentStep()));
            return;
        }

        Dinosaur escaped = candidates.get(rng.nextInt(candidates.size()));
        escaped.escape();
        lastDescription = escaped.getName() + " escapo de su recinto.";
        lastAffectedEntities = "Dinosaurio: " + escaped.getName();

        if (rng.nextDouble() < escaped.getDangerLevel()) {
            attackTouristIfPossible(state, rng, escaped);
        }

        state.getLedger().appendEvent(toRecord(state.getCurrentStep()));
    }

    private void attackTouristIfPossible(ParkState state, Random rng, Dinosaur escaped) {
        List<Tourist> touristsInPark = state.getTourists().stream()
            .filter(tourist -> tourist.getStatus() == TouristStatus.IN_PARK)
            .toList();

        if (touristsInPark.isEmpty()) {
            lastDescription += " No habia turistas en el parque.";
            return;
        }

        Tourist attacked = touristsInPark.get(rng.nextInt(touristsInPark.size()));
        attacked.setStatus(TouristStatus.ATTACKED);
        lastDescription += " " + escaped.getName() + " ataco a " + attacked.getName() + ".";
        lastAffectedEntities += ", Turista: " + attacked.getName();
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
