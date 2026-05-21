package com.axity.dinosaurpark.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Verifica que la tormenta marque evacuacion para turistas activos.
 */
class StormEventTest {

    @Test
    void executeRecordsEvacuationForTouristsInPark() {
        TestParkLedger ledger = new TestParkLedger();
        Tourist active = new Tourist(1, "Ana");
        Tourist waiting = new Tourist(2, "Luis");
        active.setStatus(TouristStatus.IN_PARK);
        ParkState state = new ParkState(
            List.of(active, waiting),
            new ArrayList<>(),
            new PowerPlant(100.0, 1.0, 0.0, 200.0, 500.0),
            ledger
        );

        StormEvent event = new StormEvent(0.04);
        event.execute(state, new Random(1));

        assertEquals("TORMENTA_TORRENCIAL", event.getName());
        assertEquals(0.04, event.getProbability());
        assertTrue(active.getVisitedZones().contains("Evacuacion"));
        assertTrue(waiting.getVisitedZones().isEmpty());
        assertEquals(1, ledger.getExpenses().size());
        assertEquals(500.0, ledger.getExpenses().get(0).amount());
        assertEquals(1, ledger.getEvents().size());
        assertTrue(ledger.getEvents().get(0).affectedEntities().contains("1"));
    }
}
