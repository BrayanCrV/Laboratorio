package com.axity.dinosaurpark.monitoring;

import com.axity.dinosaurpark.model.dinosaur.DinosaurStatus;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.simulation.ParkState;

/**
 * Panel de control que imprime un resumen del estado del parque en intervalos de tiempo.
 */
public class ParkMonitor {

    public static void displaySnapshot(ParkState state, long step, int interval) {
        if (step % interval != 0) {
            return;
        }

        long activeTourists = state.getTourists().stream()
            .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
            .count();

        long dinosaursInEnclosure = state.getDinosaurs().stream()
            .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
            .count();

        double energyPercentage = (state.getPowerPlant().getCurrentEnergy()
            / state.getPowerPlant().getInitialEnergy()) * 100.0;

        int vehiclesUnavailable = state.countVehiclesInUse();

        System.out.println("==================================================");
        System.out.println("  SNAPSHOT DEL PARQUE — Step " + step);
        System.out.println("==================================================");
        System.out.println("  1. Turistas activos:          " + activeTourists);
        System.out.println("  2. Dinosaurios en recintos:   " + dinosaursInEnclosure);
        System.out.printf ("  3. Energia disponible:        %.1f%%\n", energyPercentage);
        System.out.println("  4. Eventos activos:           " + state.getActiveEventNames());
        System.out.println("  5. Vehiculos no disponibles:  " + vehiclesUnavailable);
        System.out.println("==================================================");
    }
}
