package com.axity.dinosaurpark;

import com.axity.dinosaurpark.config.ParkConfig;
import com.axity.dinosaurpark.simulation.SimulationEngine;

/**
 * Punto de entrada del parque: carga la configuracion y arranca el motor de simulacion.
 */
public class Main {

    public static void main(String[] args) {
        ParkConfig config = ParkConfig.getInstance();
        new SimulationEngine(config).run();
    }
}
