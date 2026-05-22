package com.axity.dinosaurpark.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.config.ParkConfig;
import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import org.junit.jupiter.api.Test;

/**
 * Prueba el motor completo de simulacion end-to-end.
 */
class SimulationEngineTest {

    @Test
    void runsWithoutExceptions() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);

        // Usar una DB en memoria para no dejar archivos en disco
        engine.run("mem:test-" + System.nanoTime());
    }

    @Test
    void generatesPositiveRevenue() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);

        engine.run("mem:test-" + System.nanoTime());

        // Los ingresos deben ser mayores a 0 porque hay turistas que entran y gastan
        assertTrue(engine.getLastTotalRevenue() > 0, "La simulacion deberia generar ingresos positivos");
    }

    @Test
    void createsVehiclesAccordingToConfig() {
        ParkConfig config = ParkConfig.getInstance();
        int expectedVehicles = config.getInt("vehicles.count", 4);

        SimulationEngine engine = new SimulationEngine(config);
        engine.run("mem:test-" + System.nanoTime());

        assertEquals(expectedVehicles, engine.getLastVehicleCount(),
            "La simulacion deberia crear la cantidad configurada de vehiculos");
    }

    @Test
    void noTouristsRemainInParkAfterSimulation() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);

        engine.run("mem:test-" + System.nanoTime());

        for (Tourist tourist : engine.getLastTourists()) {
            assertTrue(
                tourist.getStatus() != TouristStatus.IN_PARK,
                "Al terminar la simulacion ningun turista deberia estar IN_PARK"
            );
        }
    }
}
