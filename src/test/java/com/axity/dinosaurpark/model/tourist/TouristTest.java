package com.axity.dinosaurpark.model.tourist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TouristTest {

    @Test
    void startsWaitingAndStoresIdentity() {
        Tourist tourist = new Tourist(1, "Ana");

        assertEquals(1, tourist.getId());
        assertEquals("Ana", tourist.getName());
        assertEquals(TouristStatus.WAITING, tourist.getStatus());
        assertEquals(0.0, tourist.getMoneySpent());
    }

    @Test
    void spendAccumulatesOnlyPositiveAmounts() {
        Tourist tourist = new Tourist(1, "Ana");

        tourist.spend(25.0);
        tourist.spend(15.5);
        tourist.spend(-10.0);
        tourist.spend(0.0);

        assertEquals(40.5, tourist.getMoneySpent());
    }

    @Test
    void recordsValidVisitsAndProtectsHistoryFromExternalChanges() {
        Tourist tourist = new Tourist(1, "Ana");

        tourist.recordVisit("Recinto Central");
        tourist.recordVisit("");
        tourist.recordVisit(null);

        assertEquals(1, tourist.getVisitedZones().size());
        assertEquals("Recinto Central", tourist.getVisitedZones().get(0));
        assertThrows(UnsupportedOperationException.class, () -> tourist.getVisitedZones().add("Otra zona"));
    }

    @Test
    void statusCanChangeDuringSimulation() {
        Tourist tourist = new Tourist(1, "Ana");

        tourist.setStatus(TouristStatus.IN_PARK);

        assertEquals(TouristStatus.IN_PARK, tourist.getStatus());
    }
}
