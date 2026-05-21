package com.axity.dinosaurpark.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParkConfigTest {

    @BeforeEach
    void resetConfig() {
        ParkConfig.resetForTesting();
    }

    @Test
    void getInstanceReturnsSameSingleton() {
        ParkConfig first = ParkConfig.getInstance();
        ParkConfig second = ParkConfig.getInstance();

        assertSame(first, second);
    }

    @Test
    void readsYamlValuesUsingDotNotation() {
        ParkConfig config = ParkConfig.getInstance();

        assertEquals(50, config.getInt("tourists", 0));
        assertEquals(100, config.getTotalSteps());
        assertEquals(25.0, config.getDouble("arrival.ticketPrice", 0.0));
        assertEquals("./data/parkdb", config.getString("db.path", ""));
    }

    @Test
    void returnsDefaultValueWhenKeyDoesNotExist() {
        ParkConfig config = ParkConfig.getInstance();

        assertEquals(7, config.getInt("missing.value", 7));
        assertEquals(3.5, config.getDouble("missing.amount", 3.5));
        assertEquals("default", config.getString("missing.text", "default"));
    }
}
