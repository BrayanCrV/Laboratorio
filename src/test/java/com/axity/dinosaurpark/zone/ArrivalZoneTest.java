package com.axity.dinosaurpark.zone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.axity.dinosaurpark.model.tourist.Tourist;
import com.axity.dinosaurpark.model.tourist.TouristStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Revisa que la entrada cobre boletos y respete la capacidad del parque.
 */
class ArrivalZoneTest {

    @Test
    void processesTouristsInBatchesAndRegistersTickets() {
        ArrivalZone arrivalZone = new ArrivalZone(3, 25.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();
        Tourist first = new Tourist(1, "Ana");
        Tourist second = new Tourist(2, "Luis");
        arrivalZone.enter(first);
        arrivalZone.enter(second);

        List<Tourist> processed = arrivalZone.processBatch(2, 0.20, ledger);

        assertEquals(2, processed.size());
        assertEquals(TouristStatus.IN_PARK, first.getStatus());
        assertEquals(20.0, first.getMoneySpent());
        assertEquals(2, arrivalZone.getCurrentOccupancy());
        assertEquals(2, ledger.getRevenues().size());
        assertEquals("TICKET", ledger.getRevenues().get(0).type());
    }

    @Test
    void doesNotProcessMoreThanAvailableCapacity() {
        ArrivalZone arrivalZone = new ArrivalZone(1, 25.0);
        InMemoryParkLedger ledger = new InMemoryParkLedger();
        arrivalZone.enter(new Tourist(1, "Ana"));
        arrivalZone.enter(new Tourist(2, "Luis"));

        List<Tourist> processed = arrivalZone.processBatch(5, 0.0, ledger);

        assertEquals(1, processed.size());
        assertEquals(1, arrivalZone.getCurrentOccupancy());
        assertEquals(1, arrivalZone.getWaitingCount());
        assertTrue(!arrivalZone.hasCapacity());
    }
}
