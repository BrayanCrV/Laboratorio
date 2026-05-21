package com.axity.dinosaurpark.zone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.tourist.Tourist;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Comprueba las visitas al centro del parque y la venta opcional de souvenirs.
 */
class CentralHubTest {

    @Test
    void visitCanSellSouvenirWithDiscount() {
        CentralHub hub = new CentralHub(5, 15.0, 1.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();
        Tourist tourist = new Tourist(1, "Ana");

        boolean visited = hub.visit(tourist, new Random(1), 0.30, ledger);

        assertTrue(visited);
        assertEquals(10.5, tourist.getMoneySpent());
        assertEquals(1, ledger.getRevenues().size());
        assertEquals("SOUVENIR", ledger.getRevenues().get(0).type());
        assertEquals(0, hub.getCurrentOccupancy());
    }

    @Test
    void visitDoesNotSellWhenProbabilityDoesNotHit() {
        CentralHub hub = new CentralHub(5, 15.0, 0.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();
        Tourist tourist = new Tourist(1, "Ana");

        boolean visited = hub.visit(tourist, new Random(1), 0.0, ledger);

        assertTrue(visited);
        assertEquals(0.0, tourist.getMoneySpent());
        assertEquals(0, ledger.getRevenues().size());
    }

    @Test
    void enterRefusesTouristWhenFull() {
        CentralHub hub = new CentralHub(1, 15.0, 0.0);
        hub.enter(new Tourist(1, "Ana"));

        boolean visited = hub.visit(new Tourist(2, "Luis"), new Random(1), 0.0, new InMemoryParkLedger());

        assertFalse(visited);
    }
}
