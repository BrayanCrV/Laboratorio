package com.axity.dinosaurpark.zone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.tourist.Tourist;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Verifica el cupo de banos, el paso del tiempo y el servicio SPA.
 */
class BathroomZoneTest {

    @Test
    void tryEnterOccupiesSlotAndCanSellSpa() {
        BathroomZone bathroom = new BathroomZone(2, 2, 20.0, 1.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();
        Tourist tourist = new Tourist(1, "Ana");

        boolean entered = bathroom.tryEnter(tourist, new Random(1), ledger);

        assertTrue(entered);
        assertEquals(1, bathroom.getCurrentOccupancy());
        assertEquals(20.0, tourist.getMoneySpent());
        assertEquals(1, ledger.getRevenues().size());
    }

    @Test
    void tickReleasesTouristAfterConfiguredDuration() {
        BathroomZone bathroom = new BathroomZone(1, 2, 20.0, 0.0);
        bathroom.tryEnter(new Tourist(1, "Ana"), new Random(1), new InMemoryParkLedger());

        bathroom.tick();
        assertEquals(1, bathroom.getCurrentOccupancy());

        bathroom.tick();
        assertEquals(0, bathroom.getCurrentOccupancy());
    }

    @Test
    void refusesEntryWhenFull() {
        BathroomZone bathroom = new BathroomZone(1, 2, 20.0, 0.0);
        bathroom.tryEnter(new Tourist(1, "Ana"), new Random(1), new InMemoryParkLedger());

        boolean entered = bathroom.tryEnter(new Tourist(2, "Luis"), new Random(1), new InMemoryParkLedger());

        assertFalse(entered);
    }
}
