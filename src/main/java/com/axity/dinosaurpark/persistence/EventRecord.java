package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

/**
 * Registro de algo importante que ocurrio durante la simulacion del parque.
 */
public record EventRecord(
    long step,
    String eventName,
    String description,
    String affectedEntities,
    LocalDateTime timestamp
) {
}
