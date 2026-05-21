package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

/**
 * Un gasto operativo del parque, desde mantenimiento hasta reparaciones.
 */
public record ExpenseRecord(
    long id,
    String type,
    double amount,
    String description,
    LocalDateTime timestamp
) {
}
