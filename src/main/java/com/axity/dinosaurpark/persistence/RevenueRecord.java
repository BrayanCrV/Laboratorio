package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

/**
 * Una entrada de dinero generada por boletos, souvenirs o experiencias del parque.
 */
public record RevenueRecord(
    long id,
    String type,
    double amount,
    int touristId,
    String zone,
    LocalDateTime timestamp
) {
}
